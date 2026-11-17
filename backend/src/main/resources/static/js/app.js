const usuario = Sessao.get();
if (!usuario) location.replace('login.html');

const $ = s => document.querySelector(s);
const app = $('#app'), modal = $('#modal');
const esc = s => String(s ?? '').replace(/[&<>"']/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));

const ESPECIES = ['Cachorro', 'Gato', 'Loro', 'Calopsita', 'Hamster', 'Outro'];
const EMOJI = { cachorro: '🐶', gato: '🐱', loro: '🦜', calopsita: '🐦', hamster: '🐹' };
const emoji = p => EMOJI[(p.especie || '').toLowerCase()] || '🐾';
const avatar = p => p.foto ? `<img src="${p.foto}" alt="Foto de ${esc(p.nome)}">` : emoji(p);
const hoje = () => new Date().toLocaleDateString('sv-SE');
const VACINAS_COMUNS = ['V10', 'V8', 'V4 (felina)', 'Antirrábica', 'Giárdia', 'Gripe canina', 'FeLV', 'Leishmaniose'];
const reduzir = (file, max = 400) => new Promise((ok, no) => {
  const img = new Image(), url = URL.createObjectURL(file);
  img.onload = () => {
    const e = Math.min(1, max / Math.max(img.width, img.height)), c = document.createElement('canvas');
    c.width = img.width * e; c.height = img.height * e;
    c.getContext('2d').drawImage(img, 0, 0, c.width, c.height);
    URL.revokeObjectURL(url); ok(c.toDataURL('image/jpeg', .8));
  };
  img.onerror = () => no(new Error('Imagem inválida'));
  img.src = url;
});
const fmtData = d => d ? new Date(d + 'T00:00').toLocaleDateString('pt-BR') : '—';
const fmtHora = d => new Date(d).toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });
const partes = d => { const x = new Date(d); return `<div class="data">${x.getDate()}<small>${x.toLocaleDateString('pt-BR', { month: 'short' }).replace('.', '')}</small></div>`; };
const vazio = t => `<p class="vazio">${t}</p>`;

function idade(nasc) {
  if (!nasc) return '';
  const m = Math.max(0, Math.floor((Date.now() - new Date(nasc + 'T00:00')) / 2629800000));
  return m < 12 ? `${m} ${m === 1 ? 'mês' : 'meses'}` : `${Math.floor(m / 12)} ${m < 24 ? 'ano' : 'anos'}`;
}

let timer;
function toast(msg, ruim) {
  const t = $('#toast');
  t.textContent = msg; t.className = 'on' + (ruim ? ' ruim' : '');
  clearTimeout(timer); timer = setTimeout(() => t.className = '', 3200);
}

function abrirForm(titulo, campos, aoEnviar) {
  modal.innerHTML = `<form><h2>${titulo}</h2>${campos}<p class="erro"></p>
    <div class="acoes"><button type="button" class="btn-texto" data-fechar>Cancelar</button><button class="btn">Salvar</button></div></form>`;
  const f = modal.querySelector('form');
  f.onsubmit = async e => {
    e.preventDefault();
    try { await aoEnviar(Object.fromEntries(new FormData(f))); modal.close(); navegar(); }
    catch (err) { f.querySelector('.erro').textContent = err.message; }
  };
  modal.showModal();
}
modal.addEventListener('click', e => { if (e.target.matches('[data-fechar]') || e.target === modal) modal.close(); });

// ---------- dados ----------
const meusPets = () => api(`/api/pets?usuarioId=${usuario.id}`);
async function minhasConsultas(pets) {
  const ids = new Set(pets.map(p => p.id));
  return (await api('/api/consultas')).filter(c => ids.has(c.petId));
}
async function minhasVacinas(pets) {
  const ids = new Set(pets.map(p => p.id));
  return (await api('/api/vacinas')).filter(v => ids.has(v.petId));
}
const opcoesPets = (pets, sel) => pets.map(p => `<option value="${p.id}" ${p.id == sel ? 'selected' : ''}>${esc(p.nome)}</option>`).join('');
const seletor = (pets, sel, rota) => `<label style="max-width:280px">Animal
  <select onchange="location.hash='#/${rota}?pet='+this.value">${opcoesPets(pets, sel)}</select></label>`;
const semPets = () => app.innerHTML = vazio('Cadastre um animal primeiro. <br><br><a class="btn" href="#/animais">Ir para Meus animais</a>');

// ---------- telas ----------
async function inicio() {
  const pets = await meusPets();
  const [cons, vac] = await Promise.all([minhasConsultas(pets), minhasVacinas(pets)]);
  const prox = cons.filter(c => new Date(c.dataHora) >= new Date());
  const doses = vac.filter(v => v.proximaDose).sort((a, b) => a.proximaDose.localeCompare(b.proximaDose)).slice(0, 5);
  app.innerHTML = `<h1>Olá, ${esc(usuario.nome.split(' ')[0])}!</h1>
    <div class="numeros"><div class="num"><b>${pets.length}</b>animais cadastrados</div>
    <div class="num"><b>${prox.length}</b>consultas agendadas</div></div>
    <div class="duas">
      <section class="painel"><h2>Próximas consultas</h2>${prox.slice(0, 5).map(c => `<div class="linha">${partes(c.dataHora)}
        <div><b>${esc(c.petNome)}</b> · ${esc(c.motivo)}<div class="suave">${esc(c.clinica)} às ${fmtHora(c.dataHora)}</div></div></div>`).join('') || vazio('Nenhuma consulta agendada.')}</section>
      <section class="painel"><h2>Próximas doses</h2>${doses.map(v => `<div class="linha">${partes(v.proximaDose + 'T00:00')}
        <div><b>${esc(v.nome)}</b> · ${esc(v.petNome)}</div>${v.proximaDose < hoje() ? '<span class="selo alerta">Atrasada</span>' : ''}</div>`).join('') || vazio('Nenhuma dose pendente.')}</section>
    </div>`;
}

let petsCache = [];
async function animais() {
  petsCache = await meusPets();
  app.innerHTML = `<div class="titulo"><h1>Meus animais</h1><button class="btn" data-acao="novo-pet">Novo animal</button></div>
    <div class="grade">${petsCache.map(p => `<article class="card"><div class="pet-topo"><div class="avatar">${avatar(p)}</div>
      <div><h3>${esc(p.nome)}</h3><span class="suave">${esc(p.especie || '')}${p.raca ? ' · ' + esc(p.raca) : ''}</span></div></div>
      <div class="suave">${p.nascimento ? `Nasceu em ${fmtData(p.nascimento)} (${idade(p.nascimento)})<br>` : ''}${p.peso ? `Peso: ${p.peso} kg` : ''}</div>
      <div class="acoes"><a class="btn-sec" href="#/historico?pet=${p.id}">Histórico</a>
      <button class="btn-sec" data-acao="editar-pet" data-id="${p.id}">Editar</button>
      <button class="btn-sec perigo" data-acao="excluir-pet" data-id="${p.id}">Excluir</button></div></article>`).join('')}</div>
    ${petsCache.length ? '' : vazio('Você ainda não cadastrou nenhum animal.')}`;
}

function formPet(p = {}) {
  let foto = p.foto || null;
  abrirForm(p.id ? 'Editar animal' : 'Novo animal', `
    <label>Foto<input type="file" accept="image/*" id="foto-in"></label>
    <img id="foto-prev" class="foto-prev" alt="Prévia da foto" ${foto ? `src="${foto}"` : 'hidden'}>
    <label>Nome<input name="nome" required value="${esc(p.nome)}"></label>
    <label>Espécie<select name="especie" required>${ESPECIES.map(e => `<option ${e === p.especie ? 'selected' : ''}>${e}</option>`).join('')}</select></label>
    <label>Raça<input name="raca" value="${esc(p.raca)}"></label>
    <label>Data de nascimento<input name="nascimento" type="date" max="${hoje()}" value="${p.nascimento || ''}"></label>
    <label>Peso (kg)<input name="peso" type="number" step="0.01" min="0" value="${p.peso ?? ''}"></label>`,
    async v => api(p.id ? `/api/pets/${p.id}` : '/api/pets', {
      method: p.id ? 'PUT' : 'POST',
      body: { ...v, foto, nascimento: v.nascimento || null, peso: v.peso ? Number(v.peso) : null, usuarioId: usuario.id }
    }));
  $('#foto-in').onchange = async e => {
    try { foto = await reduzir(e.target.files[0]); $('#foto-prev').src = foto; $('#foto-prev').hidden = false; }
    catch (err) { toast(err.message, true); }
  };
}

let vacCache = [];
async function vacinas() {
  const pets = petsCache = await meusPets();
  if (!pets.length) return semPets();
  vacCache = await minhasVacinas(pets);
  app.innerHTML = `<div class="titulo"><h1>Vacinas</h1><button class="btn" data-acao="nova-vacina">Registrar vacina</button></div>
    <section class="painel">${vacCache.map(v => `<div class="linha">${partes(v.dataAplicacao + 'T00:00')}
      <div><b>${esc(v.nome)}</b> · ${esc(v.petNome)}<div class="suave">${v.proximaDose ? 'Próxima dose: ' + fmtData(v.proximaDose) : 'Sem próxima dose'}${v.observacoes ? ' · ' + esc(v.observacoes) : ''}</div></div>
      <button class="btn-sec" data-acao="editar-vacina" data-id="${v.id}">Editar</button>
      <button class="btn-sec perigo" data-acao="excluir-vacina" data-id="${v.id}">Excluir</button></div>`).join('') || vazio('Nenhuma vacina registrada.')}</section>`;
}

let consCache = [];
async function consultas() {
  const pets = petsCache = await meusPets();
  const todas = consCache = await minhasConsultas(pets);
  const agora = new Date();
  const lista = arr => arr.map(c => `<div class="linha">${partes(c.dataHora)}
    <div><b>${esc(c.petNome)}</b> · ${esc(c.motivo)}<div class="suave">${esc(c.clinica)} às ${fmtHora(c.dataHora)}</div></div>
    <button class="btn-sec" data-acao="editar-consulta" data-id="${c.id}">Editar</button>
    <button class="btn-sec perigo" data-acao="cancelar-consulta" data-id="${c.id}">Cancelar</button></div>`).join('');
  const futuras = todas.filter(c => new Date(c.dataHora) >= agora), passadas = todas.filter(c => new Date(c.dataHora) < agora).reverse();
  app.innerHTML = `<div class="titulo"><h1>Consultas</h1><button class="btn" data-acao="nova-consulta">Agendar consulta</button></div>
    <section class="painel"><h2>Próximas</h2>${lista(futuras) || vazio('Nenhuma consulta agendada.')}</section>
    ${passadas.length ? `<section class="painel" style="margin-top:1rem"><h2>Anteriores</h2>${lista(passadas)}</section>` : ''}`;
}

function formVacina(v = {}) {
  if (!petsCache.length) return toast('Cadastre um animal antes.', true);
  abrirForm(v.id ? 'Editar vacina' : 'Registrar vacina', `
    <label>Animal<select name="petId">${opcoesPets(petsCache, v.petId)}</select></label>
    <label>Vacina<input name="nome" list="lista-vacinas" required value="${esc(v.nome)}"></label>
    <datalist id="lista-vacinas">${VACINAS_COMUNS.map(n => `<option value="${n}">`).join('')}</datalist>
    <label>Data de aplicação<input name="dataAplicacao" type="date" required max="${hoje()}" value="${v.dataAplicacao || ''}"></label>
    <label>Próxima dose<input name="proximaDose" type="date" value="${v.proximaDose || ''}"></label>
    <label>Observações<textarea name="observacoes" rows="2" maxlength="500">${esc(v.observacoes)}</textarea></label>`,
    async f => api(v.id ? `/api/vacinas/${v.id}` : '/api/vacinas', {
      method: v.id ? 'PUT' : 'POST',
      body: { ...f, petId: Number(f.petId), proximaDose: f.proximaDose || null, observacoes: f.observacoes || null }
    }));
}

function formConsulta(c = {}) {
  if (!petsCache.length) return toast('Cadastre um animal antes de agendar.', true);
  abrirForm(c.id ? 'Editar consulta' : 'Agendar consulta', `
    <label>Animal<select name="petId">${opcoesPets(petsCache, c.petId)}</select></label>
    <label>Clínica<input name="clinica" required value="${esc(c.clinica)}"></label>
    <label>Data e horário<input name="dataHora" type="datetime-local" required value="${(c.dataHora || '').slice(0, 16)}"></label>
    <label>Motivo<input name="motivo" required value="${esc(c.motivo)}"></label>`,
    async v => api(c.id ? `/api/consultas/${c.id}` : '/api/consultas', { method: c.id ? 'PUT' : 'POST', body: { ...v, petId: Number(v.petId) } }));
}

async function historico(q) {
  const pets = await meusPets();
  if (!pets.length) return semPets();
  const id = q.get('pet') || pets[0].id;
  const h = await api(`/api/pets/${id}/historico`), agora = new Date();
  app.innerHTML = `<h1>Histórico de cuidados</h1>${seletor(pets, id, 'historico')}
    <section class="painel">${h.eventos.map(e => {
      const futuro = new Date(e.data) > agora;
      return `<div class="linha">${partes(e.data)}<div><b>${esc(e.titulo)}</b>
        <div class="suave">${e.tipo === 'VACINA' ? 'Vacina' : 'Consulta'} · ${fmtData(e.data.slice(0, 10))}${e.detalhe ? ' · ' + esc(e.detalhe) : ''}</div></div>
        <span class="selo ${futuro ? 'alerta' : ''}">${futuro ? 'Agendada' : e.tipo === 'VACINA' ? 'Aplicada' : 'Realizada'}</span></div>`;
    }).join('') || vazio('Nenhum cuidado registrado ainda.')}</section>`;
}

// ---------- ações ----------
const acoes = {
  'novo-pet': () => formPet(),
  'editar-pet': id => formPet(petsCache.find(p => p.id == id)),
  'excluir-pet': async id => {
    if (!confirm('Excluir este animal?')) return;
    await api(`/api/pets/${id}`, { method: 'DELETE' }); toast('Animal excluído.'); navegar();
  },
  'nova-consulta': () => formConsulta(),
  'editar-consulta': id => formConsulta(consCache.find(c => c.id == id)),
  'cancelar-consulta': async id => {
    if (!confirm('Cancelar esta consulta?')) return;
    await api(`/api/consultas/${id}`, { method: 'DELETE' }); toast('Consulta cancelada.'); navegar();
  },
  'nova-vacina': () => formVacina(),
  'editar-vacina': id => formVacina(vacCache.find(v => v.id == id)),
  'excluir-vacina': async id => {
    if (!confirm('Excluir este registro de vacina?')) return;
    await api(`/api/vacinas/${id}`, { method: 'DELETE' }); toast('Vacina excluída.'); navegar();
},
};
app.addEventListener('click', async e => {
  const b = e.target.closest('[data-acao]');
  if (!b) return;
  try { await acoes[b.dataset.acao](b.dataset.id); }
  catch (err) { toast(err.message.includes('Erro 500') ? 'Não foi possível concluir (o registro pode ter dados vinculados).' : err.message, true); }
});

// ---------- navegação ----------
const rotas = { '': inicio, animais, vacinas, consultas, historico };
async function navegar() {
  const [r, q] = location.hash.slice(2).split('?');
  document.querySelectorAll('#menu a').forEach(a => a.classList.toggle('ativo', a.dataset.r === r));
  try { await (rotas[r] || inicio)(new URLSearchParams(q)); }
  catch (e) { app.innerHTML = vazio(esc(e.message)); }
}
$('#nome-usuario').textContent = usuario.nome;
$('#sair').onclick = Sessao.sair;
addEventListener('hashchange', navegar);
navegar();

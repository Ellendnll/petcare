// Sessão simples (guarda o usuário devolvido pelo login) e wrapper de fetch.
const Sessao = {
  get() { try { return JSON.parse(localStorage.getItem('petcare.usuario')); } catch { return null; } },
  set(u) { localStorage.setItem('petcare.usuario', JSON.stringify(u)); },
  sair() { localStorage.removeItem('petcare.usuario'); location.href = 'login.html'; }
};

// Extrai texto legível de respostas de erro, qualquer que seja o formato do handler.
function mensagemErro(d) {
  if (!d) return '';
  if (typeof d === 'string') return d;
  const lista = d.errors || d.erros || d.campos;
  if (Array.isArray(lista)) return lista.map(e => e.defaultMessage || e.message || e.mensagem || e).join('. ');
  if (lista && typeof lista === 'object') return Object.values(lista).join('. ');
  return d.message || d.mensagem || d.detail || d.erro || '';
}

async function api(url, { method = 'GET', body } = {}) {
  const r = await fetch(url, {
    method,
    headers: { 'Content-Type': 'application/json' },
    body: body ? JSON.stringify(body) : undefined
  });
  if (r.status === 204) return null;
  const dados = await r.json().catch(() => null);
  if (!r.ok) throw new Error(mensagemErro(dados) || `Erro ${r.status}`);
  return dados;
}

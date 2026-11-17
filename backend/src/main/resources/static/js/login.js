if (Sessao.get()) location.replace('index.html');

document.querySelectorAll('[data-aba]').forEach(b => b.onclick = () => {
  document.querySelectorAll('[data-aba]').forEach(x => x.setAttribute('aria-pressed', x === b));
  document.getElementById('login').hidden = b.dataset.aba !== 'login';
  document.getElementById('cadastro').hidden = b.dataset.aba !== 'cadastro';
});

const rotas = { login: '/api/usuarios/login', cadastro: '/api/usuarios' };
for (const [id, url] of Object.entries(rotas)) {
  document.getElementById(id).onsubmit = async e => {
    e.preventDefault();
    const erro = e.target.querySelector('.erro');
    erro.textContent = '';
    try {
      const u = await api(url, { method: 'POST', body: Object.fromEntries(new FormData(e.target)) });
      Sessao.set(u);
      location.href = 'index.html';
    } catch (err) { erro.textContent = err.message; }
  };
}

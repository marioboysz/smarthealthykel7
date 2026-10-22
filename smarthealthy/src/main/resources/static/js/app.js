const $ = (id) => document.getElementById(id);

/* ---- cincin progres ---- */
function setRing(el, pct) { el.style.strokeDashoffset = 327 - (327 * pct) / 100; }

/* ---- skor hero (hitung naik) ---- */
setTimeout(() => {
  setRing($('heroRing'), 82);
  let n = 0;
  const t = setInterval(() => { n += 2; $('heroNum').textContent = n; if (n >= 82) clearInterval(t); }, 24);
}, 300);

/* ---- grafik batang aktivitas ---- */
const week = [['Sen', 62], ['Sel', 78], ['Rab', 45], ['Kam', 90], ['Jum', 70], ['Sab', 95], ['Min', 55]];
const grads = [
  ['#4aa8ff', '#5ee6e0'], ['#7ef0b6', '#4aa8ff'], ['#ffd36b', '#ff8fa3'], ['#a78bfa', '#4aa8ff'],
  ['#ff8fa3', '#a78bfa'], ['#5ee6e0', '#7ef0b6'], ['#ffd36b', '#7ef0b6']
];
$('bars').innerHTML = week.map(([d, v], i) =>
  `<div class="bar-col"><span>${v}%</span><div class="bar-fill" style="--h:${v}%;background:linear-gradient(180deg,${grads[i][0]},${grads[i][1]});transition-delay:${i * 90}ms"></div><span>${d}</span></div>`
).join('');

/* ---- donat nutrisi ---- */
const nutri = [['Karbo', 45, '#4aa8ff'], ['Protein', 25, '#7ef0b6'], ['Lemak', 18, '#ffd36b'], ['Serat', 12, '#ff8fa3']];
let acc = 0;
const stops = nutri.map(([, v, c]) => { const s = `${c} ${acc}% ${acc + v}%`; acc += v; return s; });
$('donut').style.background = `conic-gradient(${stops.join(',')})`;
$('legend').innerHTML = nutri.map(([n, v, c]) => `<li><i style="background:${c}"></i>${n} ${v}%</li>`).join('');

/* ---- grafik tidur ---- */
const sleep = [6.5, 7, 5.8, 7.5, 8, 7.2, 6.9];
const days = ['Sen', 'Sel', 'Rab', 'Kam', 'Jum', 'Sab', 'Min'];
const pts = sleep.map((v, i) => [20 + i * 43.3, 105 - (v - 4) * 22]);
const line = pts.map((p, i) => (i ? 'L' : 'M') + p[0].toFixed(1) + ' ' + p[1].toFixed(1)).join(' ');
$('sleep').innerHTML =
  `<defs><linearGradient id="sg" x1="0" x2="1"><stop offset="0" stop-color="#a78bfa"/><stop offset=".5" stop-color="#4aa8ff"/><stop offset="1" stop-color="#5ee6e0"/></linearGradient>
   <linearGradient id="sa" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#4aa8ff" stop-opacity=".45"/><stop offset="1" stop-color="#4aa8ff" stop-opacity="0"/></linearGradient></defs>
   <path class="area" d="${line} L280 115 L20 115 Z" fill="url(#sa)"/><path class="line" d="${line}"/>` +
  pts.map((p, i) => `<circle cx="${p[0]}" cy="${p[1]}" r="4.5" fill="#fff"/><text x="${p[0]}" y="${p[1] - 11}">${sleep[i]}</text><text x="${p[0]}" y="128">${days[i]}</text>`).join('');

/* ---- gelas air (klik untuk isi) ---- */
let water = 3;
function drawWater() {
  $('glasses').innerHTML = Array.from({ length: 8 }, (_, i) => `<div class="gl ${i < water ? 'on' : ''}" data-i="${i}"></div>`).join('');
  $('waterTxt').textContent = `${water} / 8 gelas`;
}
$('glasses').addEventListener('click', (e) => {
  const g = e.target.closest('.gl'); if (!g) return;
  const i = +g.dataset.i + 1; water = (water === i) ? i - 1 : i; drawWater();
});
drawWater();

/* ---- checklist kebiasaan ---- */
const habits = ['Minum 8 gelas air', 'Tidur 7–8 jam', 'Jalan kaki 30 menit', 'Makan buah & sayur', 'Meditasi 10 menit'];
$('checks').innerHTML = habits.map((h, i) => `<li><label><input type="checkbox" ${i < 2 ? 'checked' : ''}><span>${h}</span></label></li>`).join('');
function updateHabit() {
  const all = document.querySelectorAll('#checks input');
  const pct = Math.round([...all].filter((c) => c.checked).length / all.length * 100);
  setRing($('habitRing'), pct); $('habitNum').textContent = pct + '%';
}
$('checks').addEventListener('change', updateHabit);

/* ---- animasi saat di-scroll ---- */
const io = new IntersectionObserver((es) => es.forEach((e) => {
  if (e.isIntersecting) { e.target.classList.add('in'); if (e.target.classList.contains('habit-box')) updateHabit(); io.unobserve(e.target); }
}), { threshold: 0.25 });
document.querySelectorAll('.reveal').forEach((el) => io.observe(el));

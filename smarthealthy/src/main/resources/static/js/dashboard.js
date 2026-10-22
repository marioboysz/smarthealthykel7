const data = JSON.parse(document.getElementById('chartData').dataset.json);
const grads = {
  AKTIVITAS: ['#ffd36b', '#ff8fa3'], AIR: ['#4aa8ff', '#5ee6e0'],
  NUTRISI: ['#7ef0b6', '#4aa8ff'], TIDUR: ['#a78bfa', '#4aa8ff']
};

/* grafik batang 7 hari */
document.querySelectorAll('.bars[data-j]').forEach((el) => {
  const vals = data[el.dataset.j], max = Math.max(...vals, 1), [a, b] = grads[el.dataset.j];
  el.innerHTML = vals.map((v, i) =>
    `<div class="bar-col"><span>${v || ''}</span><div class="bar-fill" style="--h:${v ? Math.max(v / max * 100, 8) : 2}%;background:linear-gradient(180deg,${a},${b});transition-delay:${i * 80}ms"></div><span>${data.labels[i]}</span></div>`
  ).join('');
});

/* donat komposisi aktivitas */
const palette = ['#4aa8ff', '#7ef0b6', '#ffd36b', '#ff8fa3', '#a78bfa', '#5ee6e0'];
const komp = Object.entries(data.komposisi);
const total = komp.reduce((a, [, v]) => a + v, 0);
const donut = document.getElementById('donut'), legend = document.getElementById('legend');
if (total > 0) {
  let acc = 0;
  donut.style.background = 'conic-gradient(' + komp.map(([, v], i) => {
    const s = `${palette[i % 6]} ${acc}% ${acc + v / total * 100}%`; acc += v / total * 100; return s;
  }).join(',') + ')';
  document.getElementById('donutTotal').innerHTML = `${Math.round(total)}<small>menit</small>`;
  legend.innerHTML = komp.map(([n, v], i) =>
    `<li><i style="background:${palette[i % 6]}"></i>${n} · ${Math.round(v)} mnt</li>`).join('');
} else {
  donut.style.background = '#e3eefa';
  legend.innerHTML = '<li>Belum ada aktivitas minggu ini</li>';
}

/* satuan ikut berubah sesuai jenis kegiatan */
const sel = document.getElementById('detail'), unit = document.getElementById('unit');
const setUnit = () => { unit.textContent = sel.selectedOptions[0].dataset.unit; };
sel.addEventListener('change', setUnit); setUnit();

/* tanggal default = hari ini */
const d = new Date(); d.setMinutes(d.getMinutes() - d.getTimezoneOffset());
const tgl = document.getElementById('tgl'); tgl.value = d.toISOString().slice(0, 10); tgl.max = tgl.value;

document.querySelectorAll('.reveal').forEach((el, i) => setTimeout(() => el.classList.add('in'), 100 + i * 80));

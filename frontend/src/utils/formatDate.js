export default function formatDate(value, locale = 'es') {
  if (!value) return '-';
  try {
    const d = value instanceof Date ? value : new Date(value);
    if (isNaN(d)) return String(value);
    const date = new Intl.DateTimeFormat(locale, { day: 'numeric', month: 'numeric', year: 'numeric' }).format(d);
    const time = new Intl.DateTimeFormat(locale, { hour: '2-digit', minute: '2-digit', second: '2-digit', hour12: false }).format(d);
    return `${date}, ${time}`;
  } catch (e) {
    return String(value);
  }
}

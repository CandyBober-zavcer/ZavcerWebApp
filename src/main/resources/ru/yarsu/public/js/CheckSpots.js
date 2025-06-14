document.addEventListener('DOMContentLoaded', function () {
    const blockedData = {
        'Точка 1': {
            '2025-06-15': [
                { start: '9:00', end: '12:00', user: 'Анна Кузнецова' },
                { start: '15:00', end: '17:00', user: 'Дмитрий Орлов' },
            ],
            '2025-06-18': [
                { start: '10:00', end: '11:30', user: 'Иван Иванов' }
            ]
        },
        'Точка 2': {
            '2025-06-16': [
                { start: '9:00', end: '12:00', user: 'Елена Федорова' }
            ],
            '2025-06-15': [
                { start: '9:00', end: '12:00', user: 'Анна Кузнецова' },
                { start: '15:00', end: '17:00', user: 'Дмитрий Орлов' },
            ],
        }
    };

    const calendarDays = document.getElementById('calendarDays');
    const currentMonthYear = document.getElementById('currentMonthYear');
    const prevMonthBtn = document.getElementById('prevMonth');
    const nextMonthBtn = document.getElementById('nextMonth');
    const timeSlots = document.querySelector('.time-slots');
    const timePicker = document.querySelector('.time-picker');
    const blockedSlotsInfo = document.getElementById('blockedSlotsInfo');

    const today = new Date();
    today.setHours(0, 0, 0, 0);
    let currentDate = new Date();
    currentDate.setDate(1);
    let selectedDate = null;

    const timeRange = ['09:00', '10:00', '11:00', '12:00', '13:00', '14:00', '15:00', '16:00', '17:00'];

    function formatDate(date) {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
    }

    function initCalendar() {
        renderCalendar();
        prevMonthBtn.addEventListener('click', () => {
            currentDate.setMonth(currentDate.getMonth() - 1);
            renderCalendar();
        });
        nextMonthBtn.addEventListener('click', () => {
            currentDate.setMonth(currentDate.getMonth() + 1);
            renderCalendar();
        });
    }

    function renderCalendar() {
        const year = currentDate.getFullYear();
        const month = currentDate.getMonth();
        currentMonthYear.textContent = new Intl.DateTimeFormat('ru-RU', {
            month: 'long',
            year: 'numeric'
        }).format(currentDate);

        const firstDay = new Date(year, month, 1);
        const lastDay = new Date(year, month + 1, 0);
        const firstDayOfWeek = (firstDay.getDay() || 7) - 1;
        const daysInMonth = lastDay.getDate();

        calendarDays.innerHTML = '';

        for (let i = 0; i < firstDayOfWeek; i++) {
            const empty = document.createElement('div');
            empty.className = 'calendar-day other-month';
            calendarDays.appendChild(empty);
        }

        for (let i = 1; i <= daysInMonth; i++) {
            const date = new Date(year, month, i);
            date.setHours(0, 0, 0, 0);
            const dateStr = formatDate(date);

            const dayEl = document.createElement('div');
            dayEl.className = `calendar-day ${date < today ? 'past' : 'available'}`;
            dayEl.textContent = i;

           
            let hasBlocked = false;
            for (const loc in blockedData) {
                if (blockedData[loc][dateStr]) {
                    hasBlocked = true;
                    break;
                }
            }

            if (hasBlocked) {
                dayEl.classList.add('blocked-date');
            }

            if (date >= today) {
                dayEl.style.cursor = 'pointer';
                dayEl.addEventListener('click', () => {
                    document.querySelectorAll('.calendar-day').forEach(el => el.classList.remove('selected'));
                    dayEl.classList.add('selected');
                    selectedDate = dateStr;

                    showBlockedSlotsInfo(selectedDate);
                });
            }

            calendarDays.appendChild(dayEl);
        }
    }

    function showBlockedSlotsInfo(dateStr) {
        let html = `<strong>Записи на ${dateStr}:</strong>`;
        let hasData = false;

        for (const loc in blockedData) {
            if (blockedData[loc][dateStr]) {
                html += `<p><strong>${loc}:</strong></p><ul>`;
                blockedData[loc][dateStr].forEach(slot => {
                    html += `<li>${slot.start}–${slot.end} — ${slot.user}</li>`;
                });
                html += `</ul>`;
                hasData = true;
            }
        }

        blockedSlotsInfo.innerHTML = hasData ? html : 'Нет записей на выбранную дату';
        blockedSlotsInfo.style.display = 'block';
        timePicker.style.display = 'none';
        timeSlots.innerHTML = '';
    }


    initCalendar();
});

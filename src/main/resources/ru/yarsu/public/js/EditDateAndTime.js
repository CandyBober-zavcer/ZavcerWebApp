document.addEventListener('DOMContentLoaded', function () {
    const blockedDates = {
        '2025-06-15': ['10:00', '11:00', '12:00'],
        '2025-06-16': ['14:00', '15:00', '16:00'],
    };
    const freeDates = {
        '2025-06-17': ['10:00', '11:00', '12:00'],
        '2025-06-18': ['14:00', '15:00', '16:00'],
    };
    const calendarDays = document.getElementById('calendarDays');
    const currentMonthYear = document.getElementById('currentMonthYear');
    const prevMonthBtn = document.getElementById('prevMonth');
    const nextMonthBtn = document.getElementById('nextMonth');
    const timePicker = document.querySelector('.time-picker');
    const timeSlots = document.querySelector('.time-slots');
    const resetTimeBtn = document.querySelector('.reset-time');
    const saveScheduleBtn = document.getElementById('saveScheduleBtn');

    const today = new Date();
    today.setHours(0, 0, 0, 0);
    let currentDate = new Date();
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

            if (blockedDates[dateStr] || freeDates[dateStr]) {
                dayEl.classList.add('has-slots');
            }

            if (date >= today) {
                dayEl.addEventListener('click', () => {
                    document.querySelectorAll('.calendar-day').forEach(el => el.classList.remove('selected'));
                    dayEl.classList.add('selected');
                    selectedDate = dateStr;
                    timePicker.style.display = 'block';
                    generateTimeSlots();
                });
            }

            calendarDays.appendChild(dayEl);
        }
    }

    function generateTimeSlots() {
        timeSlots.innerHTML = '';

        const blockedSlots = blockedDates[selectedDate] || [];
        const freeSlots = freeDates[selectedDate] || [];

        timeRange.forEach(time => {
            const slot = document.createElement('div');
            slot.className = 'time-slot';
            slot.textContent = time;

            if (blockedSlots.includes(time)) {
                slot.classList.add('blocked');
                slot.style.cursor = 'not-allowed';
                slot.style.backgroundColor = '#dc3545';
                slot.style.color = 'white';
            } else if (freeSlots.includes(time)) {
                slot.classList.add('free');
                slot.style.cursor = 'pointer';
                slot.style.backgroundColor = '#0d6efd';
                slot.style.color = 'white';
            } else {
                slot.classList.add('available');
                slot.style.cursor = 'pointer';
                slot.style.backgroundColor = '#0d6efd';
                slot.style.color = 'white';
            }

            slot.addEventListener('click', () => {
                if (blockedSlots.includes(time)) return;

                if (freeSlots.includes(time)) {
                    freeDates[selectedDate] = freeDates[selectedDate].filter(t => t !== time);
                    if (freeDates[selectedDate].length === 0) {
                        delete freeDates[selectedDate];
                    }
                } else {
                    if (!freeDates[selectedDate]) {
                        freeDates[selectedDate] = [];
                    }
                    freeDates[selectedDate].push(time);
                    freeDates[selectedDate].sort();
                }
                generateTimeSlots();
            });

            timeSlots.appendChild(slot);
        });
    }

    resetTimeBtn.addEventListener('click', () => {
        if (selectedDate && freeDates[selectedDate]) {
            delete freeDates[selectedDate];
            generateTimeSlots();
        }
    });

    saveScheduleBtn.addEventListener('click', () => {
        console.log('Отправляемые свободные слоты:', freeDates);

        fetch('http://localhost:8080/teacher/куда?', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(freeDates)
        })
            .then(res => {
                if (!res.ok) throw new Error("Ошибка отправки");
                return res.json();
            })
            .then(data => {
                alert('Свободное расписание успешно сохранено!');
            })
            .catch(err => {
                console.error(err);
                alert('Ошибка при сохранении расписания');
            });
    });

    initCalendar();
});

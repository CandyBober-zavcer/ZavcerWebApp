document.addEventListener('DOMContentLoaded', function () {
    const freeDates = {
            '2025-06-17': ['10:00', '11:00', '12:00'],
            '2025-06-18': ['14:00', '15:00', '16:00'],
        };
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

    const blockedData = blockedSlotsOwner;
    const freeDates = freeSlotsOwner;
    console.log(blockedData)
    console.log(freeDates)

    const locationSelect = document.getElementById('locationSelect');
    const calendarDays = document.getElementById('calendarDays');
    const currentMonthYear = document.getElementById('currentMonthYear');
    const prevMonthBtn = document.getElementById('prevMonth');
    const nextMonthBtn = document.getElementById('nextMonth');
    const timePicker = document.querySelector('.time-picker');
    const timeSlots = document.querySelector('.time-slots');
    const resetTimeBtn = document.querySelector('.reset-time');
    const saveScheduleBtn = document.getElementById('saveScheduleBtn');

    let blockedSlotsInfo = document.getElementById('blockedSlotsInfo');
    if (!blockedSlotsInfo) {
        blockedSlotsInfo = document.createElement('div');
        blockedSlotsInfo.id = 'blockedSlotsInfo';
        blockedSlotsInfo.style.marginTop = '15px';
        blockedSlotsInfo.style.backgroundColor = '#f8f9fa';
        blockedSlotsInfo.style.border = '1px solid #dee2e6';
        blockedSlotsInfo.style.padding = '10px';
        blockedSlotsInfo.style.borderRadius = '5px';
        blockedSlotsInfo.style.display = 'none';
        timePicker.parentNode.insertBefore(blockedSlotsInfo, timePicker.nextSibling);
    }

    const today = new Date();
    today.setHours(0, 0, 0, 0);
    let currentDate = new Date();
    currentDate.setDate(1);
    let selectedDate = null;
    let currentLocation = null;

    const timeRange = ['09:00', '10:00', '11:00', '12:00', '13:00', '14:00', '15:00', '16:00', '17:00', '18:00', '19:00', '20:00', '21:00', '22:00', '23:00'];

    function formatDate(date) {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
    }

    function populateLocations() {
        locationSelect.innerHTML = '';
        const locations = Object.keys(freeDates);
        locations.forEach(loc => {
            const option = document.createElement('option');
            option.value = loc;
            option.textContent = loc;
            locationSelect.appendChild(option);
        });
        currentLocation = locationSelect.value;
    }

    locationSelect.addEventListener('change', () => {
        currentLocation = locationSelect.value;
        renderCalendar();
        resetSelection();
    });

    function initCalendar() {
        populateLocations();
        renderCalendar();
        prevMonthBtn.addEventListener('click', () => {
            currentDate.setMonth(currentDate.getMonth() - 1);
            renderCalendar();
            resetSelection();
        });
        nextMonthBtn.addEventListener('click', () => {
            currentDate.setMonth(currentDate.getMonth() + 1);
            renderCalendar();
            resetSelection();
        });
    }

    function resetSelection() {
        selectedDate = null;
        timePicker.style.display = 'none';
        blockedSlotsInfo.style.display = 'none';
        timeSlots.innerHTML = '';
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

            if (blockedData[currentLocation]?.[dateStr]) {
                dayEl.classList.add('blocked-date');
            } else if (freeDates[currentLocation]?.[dateStr]) {
                dayEl.classList.add('has-slots');
            }

            if (date >= today) {
                dayEl.style.cursor = 'pointer';
                dayEl.addEventListener('click', () => {
                    document.querySelectorAll('.calendar-day').forEach(el => el.classList.remove('selected'));
                    dayEl.classList.add('selected');
                    selectedDate = dateStr;

                    if (blockedData[currentLocation]?.[selectedDate]) {
                        showBlockedSlotsInfo(selectedDate);
                    } else {
                        hideBlockedSlotsInfo();
                    }

                    timePicker.style.display = 'block';
                    generateTimeSlots();
                });
            }

            calendarDays.appendChild(dayEl);
        }
    }

    function generateTimeSlots() {
        timeSlots.innerHTML = '';
        const blocked = blockedData[currentLocation]?.[selectedDate] || [];
        const free = freeDates[currentLocation]?.[selectedDate] || [];

        timeRange.forEach(time => {
            const slot = document.createElement('div');
            slot.className = 'time-slot';
            slot.textContent = time;

            const isBlocked = blocked.some(({ start, end }) => {
                const [sh, sm] = start.split(':').map(Number);
                const [eh, em] = end.split(':').map(Number);
                const slotTime = parseInt(time.split(':')[0], 10);
                return slotTime >= sh && slotTime < eh;
            });

            if (isBlocked) {
                slot.classList.add('blocked');
            } else if (free.includes(time)) {
                slot.classList.add('free');
            } else {
                slot.classList.add('available');
            }

            slot.addEventListener('click', () => {
                if (isBlocked) return;
                const times = freeDates[currentLocation] ||= {};
                const list = times[selectedDate] ||= [];

                const idx = list.indexOf(time);
                if (idx > -1) list.splice(idx, 1);
                else list.push(time);

                if (list.length === 0) delete times[selectedDate];
                generateTimeSlots();
            });

            timeSlots.appendChild(slot);
        });
    }

    resetTimeBtn.addEventListener('click', () => {
        if (selectedDate && freeDates[currentLocation]?.[selectedDate]) {
            delete freeDates[currentLocation][selectedDate];
            generateTimeSlots();
        }
    });

    saveScheduleBtn.addEventListener('click', (e) => {
        e.preventDefault();
        console.log('Отправляемые свободные слоты:', freeDates);

        fetch('/schedule/owner/editing/${userId}', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(freeDates)
        })
            .then(res => {
                if (!res.ok) throw new Error("Ошибка отправки");
                return res.json();
            })
            .then(() => alert('Расписание сохранено успешно!'))
            .catch(err => {
                alert('Ошибка при сохранении расписания');
                console.error(err);
            });
    });

    function showBlockedSlotsInfo(dateStr) {
        const slots = blockedData[currentLocation]?.[dateStr] || [];
        let html = '<strong>Забронированные часы:</strong><ul>';
        slots.forEach(slot => {
            html += `<li>${slot.start}–${slot.end} — ${slot.user}</li>`;
        });
        html += '</ul>';
        blockedSlotsInfo.innerHTML = html;
        blockedSlotsInfo.style.display = 'block';
    }

    function hideBlockedSlotsInfo() {
        blockedSlotsInfo.style.display = 'none';
        blockedSlotsInfo.innerHTML = '';
    }

    initCalendar();
});

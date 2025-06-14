document.addEventListener('DOMContentLoaded', function () {
    if (document.getElementById('calendarTeacher')) {
        const blockedDates = {
                '2025-06-15': [
                    { time: '10:00', user: 'Иван Иванов' },
                    { time: '11:00', user: 'Мария Петрова' },
                    { time: '12:00', user: 'Сергей Смирнов' }
                ],
                '2025-06-16': [
                    { time: '14:00', user: 'Анна Кузнецова' },
                    { time: '15:00', user: 'Дмитрий Орлов' },
                    { time: '16:00', user: 'Елена Федорова' }
                ],
            };

        const freeDates = {
                '2025-06-17': ['10:00', '11:00', '12:00'],
                '2025-06-18': ['14:00', '15:00', '16:00'],
            };

        const calendarDays = document.getElementById('calendarDaysTeacher');
        const currentMonthYear = document.getElementById('currentMonthYearTeacher');
        const prevMonthBtn = document.getElementById('prevMonthTeacher');
        const nextMonthBtn = document.getElementById('nextMonthTeacher');

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

        const timeRange = ['09:00', '10:00', '11:00', '12:00', '13:00', '14:00', '15:00', '16:00', '17:00', '18:00', '19:00', '20:00', '21:00', '22:00', '23:00'];

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
                currentDate.setDate(1);
                renderCalendar();
                resetSelection();
            });
            nextMonthBtn.addEventListener('click', () => {
                currentDate.setMonth(currentDate.getMonth() + 1);
                currentDate.setDate(1);
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

                if (blockedDates[dateStr]) {
                    dayEl.classList.add('blocked-date');
                } else if (freeDates[dateStr]) {
                    dayEl.classList.add('has-slots');
                }

                if (date >= today) {
                    dayEl.style.cursor = 'pointer';
                    dayEl.addEventListener('click', () => {
                        document.querySelectorAll('.calendar-day').forEach(el => el.classList.remove('selected'));
                        dayEl.classList.add('selected');
                        selectedDate = dateStr;

                        if (blockedDates[selectedDate]) {
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

            const blockedSlots = blockedDates[selectedDate] || [];
            const freeSlots = freeDates[selectedDate] || [];

            timeRange.forEach(time => {
                const slot = document.createElement('div');
                slot.className = 'time-slot';
                slot.textContent = time;

                const isBlocked = blockedSlots.some(slotObj => slotObj.time === time);
                if (isBlocked) {
                    slot.classList.add('blocked');
                    slot.style.cursor = 'not-allowed';
                    slot.style.backgroundColor = '#f8f9fa';
                    slot.style.color = 'white';
                } else if (freeSlots.includes(time)) {
                    slot.classList.add('free');
                    slot.style.cursor = 'pointer';
                    slot.style.backgroundColor = 'red';
                    slot.style.color = 'white';
                } else {
                    slot.classList.add('available');
                    slot.style.cursor = 'pointer';
                    slot.style.backgroundColor = 'white';
                    slot.style.color = 'black';
                    slot.style.border = '1px solid #dee2e6';
                }

                slot.addEventListener('click', () => {
                    if (isBlocked) return;

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

        saveScheduleBtn.addEventListener('click', (e) => {
            e.preventDefault();

            console.log('Отправляемые свободные слоты:', freeDates);

            fetch('schedule/teacher/${userId}', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(freeDates)
            })
                .then(res => {
                    if (!res.ok) throw new Error("Ошибка отправки");
                    return res.json();
                })
                .then(data => {
                    alert('Расписание сохранено успешно!');
                })
                .catch(err => {
                    alert('Ошибка при сохранении расписания');
                    console.error(err);
                });
        });

        function showBlockedSlotsInfo(dateStr) {
            const slots = blockedDates[dateStr];
            let html = '<strong>Забронированные часы:</strong><ul>';
            slots.forEach(slot => {
                html += `<li>${slot.time} — ${slot.user}</li>`;
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
    }
});

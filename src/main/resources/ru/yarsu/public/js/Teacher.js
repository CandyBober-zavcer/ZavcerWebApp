document.addEventListener('DOMContentLoaded', function () {
    const availableDates = {
        '2025-06-15': ['10:00', '11:00', '12:00'],
        '2025-06-16': ['14:00', '15:00', '16:00'],
    };

    const bookedDates = {};

    const calendarDays = document.getElementById('calendarDays');
    const currentMonthYear = document.getElementById('currentMonthYear');
    const prevMonthBtn = document.getElementById('prevMonth');
    const nextMonthBtn = document.getElementById('nextMonth');
    const timePicker = document.querySelector('.time-picker');
    const timeSlots = document.querySelector('.time-slots');
    const bookingSummary = document.querySelector('.booking-summary');
    const pricePerHour = 500;

    if (!calendarDays || !currentMonthYear || !prevMonthBtn || !nextMonthBtn) {
        console.error("Один из элементов календаря не найден!");
        return;
    }

    // Текущая дата
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    let currentDate = new Date();
    currentDate.setHours(0, 0, 0, 0);
    let selectedDate = null;
    let selectedTime = null;

    // Инициализация календаря
    function initCalendar() {
        renderCalendar();

        prevMonthBtn.addEventListener('click', () => {
            if (!prevMonthBtn.classList.contains('disabled')) {
                currentDate.setMonth(currentDate.getMonth() - 1);
                renderCalendar();
            }
        });

        nextMonthBtn.addEventListener('click', () => {
            if (!nextMonthBtn.classList.contains('disabled')) {
                currentDate.setMonth(currentDate.getMonth() + 1);
                renderCalendar();
            }
        });
    }

    // Отрисовка календаря
    function renderCalendar() {
        const year = currentDate.getFullYear();
        const month = currentDate.getMonth();

        currentMonthYear.textContent = new Intl.DateTimeFormat('ru-RU', {
            month: 'long',
            year: 'numeric'
        }).format(currentDate);

        const firstDay = new Date(year, month, 1);
        const lastDay = new Date(year, month + 1, 0);
        const firstDayOfWeek = firstDay.getDay() === 0 ? 6 : firstDay.getDay() - 1;
        const daysInMonth = lastDay.getDate();

        calendarDays.innerHTML = '';

        // Пустые ячейки предыдущего месяца
        for (let i = 0; i < firstDayOfWeek; i++) {
            const dayElement = document.createElement('div');
            dayElement.className = 'calendar-day other-month';
            calendarDays.appendChild(dayElement);
        }

        // Дни текущего месяца
        for (let i = 1; i <= daysInMonth; i++) {
            const dayDate = new Date(year, month, i);
            dayDate.setHours(0, 0, 0, 0);
            const dateStr = formatDate(dayDate);
            const bookedHours = bookedDates[dateStr] || [];
            const availableHours = availableDates[dateStr] || [];
            const isPast = dayDate < today;
            const isUnavailable = availableHours.length === 0 ||
                (availableHours.length === bookedHours.length && bookedHours.length > 0);

            const dayElement = document.createElement('div');
            dayElement.className = `calendar-day ${isPast ? 'past' :
                isUnavailable ? 'unavailable' :
                    'available'
                }`;
            dayElement.textContent = i;

            if (!isPast && !isUnavailable) {
                dayElement.addEventListener('click', () => {
                    document.querySelectorAll('.calendar-day').forEach(el => el.classList.remove('selected'));
                    dayElement.classList.add('selected');
                    selectedDate = dateStr;
                    selectedTime = null;

                    timePicker.style.display = 'block';
                    bookingSummary.style.display = 'none';
                    updateSummary();
                    generateTimeSlots();
                });
            }

            calendarDays.appendChild(dayElement);
        }

        // Пустые ячейки следующего месяца
        const totalCells = Math.ceil((firstDayOfWeek + daysInMonth) / 7) * 7;
        const remainingCells = totalCells - (firstDayOfWeek + daysInMonth);

        for (let i = 0; i < remainingCells; i++) {
            const dayElement = document.createElement('div');
            dayElement.className = 'calendar-day other-month';
            calendarDays.appendChild(dayElement);
        }

        // Блокировка кнопок навигации
        const prevMonth = new Date(year, month - 1, 1);
        const isPrevMonthPast = prevMonth < new Date(today.getFullYear(), today.getMonth(), 1);
        prevMonthBtn.classList.toggle('disabled', isPrevMonthPast);

        const maxAllowedDate = new Date();
        maxAllowedDate.setFullYear(today.getFullYear() + 1);
        const nextMonth = new Date(year, month + 1, 1);
        const isNextMonthTooFar = nextMonth > maxAllowedDate;
        nextMonthBtn.classList.toggle('disabled', isNextMonthTooFar);
    }

    // Форматирование даты
    function formatDate(date) {
        return date.toISOString().split('T')[0];
    }

    // Генерация временных слотов
    function generateTimeSlots() {
        const availableTimesForDate = availableDates[selectedDate] || [];
        const bookedTimesForDate = bookedDates[selectedDate] || [];

        timeSlots.innerHTML = '';
        selectedTime = null;

        availableTimesForDate.forEach(time => {
            const isBooked = bookedTimesForDate.includes(time);
            const timeSlot = document.createElement('div');
            timeSlot.className = `time-slot ${isBooked ? 'unavailable' : 'available'}`;
            timeSlot.textContent = time;

            if (!isBooked) {
                timeSlot.addEventListener('click', function () {
                    document.querySelectorAll('.time-slot').forEach(el => el.classList.remove('selected'));
                    this.classList.add('selected');
                    selectedTime = time;
                    bookingSummary.style.display = 'block';
                    updateSummary();
                });
            }

            timeSlots.appendChild(timeSlot);
        });
    }

    // Обновление сводки
    function updateSummary() {
        if (selectedDate) {
            const date = new Date(selectedDate);
            document.querySelector('.summary-date').textContent =
                `${date.getDate()}.${date.getMonth() + 1}.${date.getFullYear()}`;
        }

        if (selectedTime) {
            document.querySelector('.summary-time').textContent = selectedTime;
            document.querySelector('.summary-price').textContent = pricePerHour;
        } else {
            document.querySelector('.summary-time').textContent = '(выберите время)';
            document.querySelector('.summary-price').textContent = '...';
        }
    }

    // Обработка формы
    document.getElementById('bookingForm').addEventListener('submit', function (e) {
        e.preventDefault();

        if (!selectedDate || !selectedTime) {
            alert('Пожалуйста, выберите дату и время бронирования');
            return;
        }

        // Проверка авторизации
        const isAuthorized = !!window.userId;
        if (!isAuthorized) {
            alert("Необходима авторизация");
            return;
        }

        const formData = {
            date: selectedDate,
            time: selectedTime,
            userId: window.userId // Добавляем ID пользователя
        };

        console.log('Отправляемые данные:', formData);
        fetch('http://localhost:8080/studio/1', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        })
        .then(response => {
            if (!response.ok) throw new Error("Ошибка при отправке");
            return response.json();
        })
        .then(data => {
            alert(`Студия успешно забронирована на ${selectedDate} в ${selectedTime}`);

            // Обновляем локальный список бронирований
            if (!bookedDates[selectedDate]) {
                bookedDates[selectedDate] = [];
            }
            bookedDates[selectedDate].push(selectedTime);

            // Обновляем интерфейс
            generateTimeSlots();
            renderCalendar();
        })
        .catch(error => {
            alert("Ошибка при бронировании.");
            console.error(error);
        });
    });

    // Запуск календаря
    initCalendar();
});

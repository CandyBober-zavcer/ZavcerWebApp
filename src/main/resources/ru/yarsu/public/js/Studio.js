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
    const resetTimeBtn = document.querySelector('.reset-time');
    const pricePerHour = 500;

    // Текущая дата без времени (для сравнения)
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    let currentDate = new Date();
    currentDate.setHours(0, 0, 0, 0);
    let selectedDate = null;
    let startTime = null;
    let endTime = null;

    // Инициализация календаря
    function initCalendar() {
        renderCalendar();

        // Обработчики для кнопок переключения месяцев
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

    // Рендер календаря
    function renderCalendar() {
        const year = currentDate.getFullYear();
        const month = currentDate.getMonth();

        // Установка заголовка (месяц и год)
        currentMonthYear.textContent = new Intl.DateTimeFormat('ru-RU', {
            month: 'long',
            year: 'numeric'
        }).format(currentDate);

        // Первый день месяца
        const firstDay = new Date(year, month, 1);
        // Последний день месяца
        const lastDay = new Date(year, month + 1, 0);
        // День недели первого дня месяца (0 - воскресенье, 1 - понедельник и т.д.)
        const firstDayOfWeek = firstDay.getDay() === 0 ? 6 : firstDay.getDay() - 1;
        // Количество дней в месяце
        const daysInMonth = lastDay.getDate();

        // Очищаем календарь
        calendarDays.innerHTML = '';

        // Добавляем пустые ячейки для дней предыдущего месяца
        for (let i = 0; i < firstDayOfWeek; i++) {
            const dayElement = document.createElement('div');
            dayElement.className = 'calendar-day other-month';
            calendarDays.appendChild(dayElement);
        }

        // Добавляем дни текущего месяца
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

                    // Показываем таймпикер
                    timePicker.style.display = 'block';
                    bookingSummary.style.display = 'none';
                    resetTimeBtn.style.display = 'none';

                    // Обновляем сводку
                    updateSummary();

                    // Генерируем временные слоты
                    generateTimeSlots();
                });
            }

            calendarDays.appendChild(dayElement);
        }

        // Добавляем пустые ячейки для дней следующего месяца
        const totalCells = Math.ceil((firstDayOfWeek + daysInMonth) / 7) * 7;
        const remainingCells = totalCells - (firstDayOfWeek + daysInMonth);

        for (let i = 0; i < remainingCells; i++) {
            const dayElement = document.createElement('div');
            dayElement.className = 'calendar-day other-month';
            calendarDays.appendChild(dayElement);
        }

        // Блокируем кнопку "предыдущий месяц", если это прошедший месяц
        const prevMonth = new Date(year, month - 1, 1);
        const isPrevMonthPast = prevMonth < new Date(today.getFullYear(), today.getMonth(), 1);

        if (isPrevMonthPast) {
            prevMonthBtn.classList.add('disabled');
        } else {
            prevMonthBtn.classList.remove('disabled');
        }

        // Блокируем кнопку "следующий месяц", если это месяц через год
        const maxAllowedDate = new Date();
        maxAllowedDate.setFullYear(today.getFullYear() + 1);
        const nextMonth = new Date(year, month + 1, 1);
        const isNextMonthTooFar = nextMonth > maxAllowedDate;

        if (isNextMonthTooFar) {
            nextMonthBtn.classList.add('disabled');
        } else {
            nextMonthBtn.classList.remove('disabled');
        }
    }

    // Форматирование даты в YYYY-MM-DD
    function formatDate(date) {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
    }

    // Генерация временных слотов
    function generateTimeSlots() {
        const availableTimesForDate = availableDates[selectedDate] || [];
        timeSlots.innerHTML = '';
        startTime = null;
        endTime = null;

        availableTimesForDate.forEach(time => {
            const timeSlot = document.createElement('div');
            timeSlot.className = 'time-slot available';
            timeSlot.textContent = time;

            timeSlot.addEventListener('click', function () {
                // Если уже есть startTime и endTime, сбрасываем выбор
                if (startTime && endTime) {
                    resetTimeSelection();
                    return;
                }

                // Если кликаем на уже выбранное startTime, сбрасываем выбор
                if (startTime === time && !endTime) {
                    resetTimeSelection();
                    return;
                }

                if (!startTime) {
                    // Выбираем время начала
                    document.querySelectorAll('.time-slot').forEach(el => el.classList.remove('selected'));
                    this.classList.add('selected');
                    startTime = time;
                    resetTimeBtn.style.display = 'block';

                    // Показываем сводку
                    bookingSummary.style.display = 'block';
                } else {
                    // Выбираем время окончания
                    const startIndex = availableTimesForDate.indexOf(startTime);
                    const endIndex = availableTimesForDate.indexOf(time);

                    if (endIndex > startIndex) {
                        endTime = time;

                        // Выделяем выбранный диапазон
                        document.querySelectorAll('.time-slot').forEach(el => el.classList.remove('selected'));
                        for (let i = startIndex; i <= endIndex; i++) {
                            document.querySelectorAll('.time-slot')[i].classList.add('selected');
                        }
                    } else {
                        // Если выбрано время раньше стартового, делаем его новым стартовым
                        resetTimeSelection();
                        this.classList.add('selected');
                        startTime = time;
                        resetTimeBtn.style.display = 'block';
                    }
                }

                updateSummary();
            });

            timeSlots.appendChild(timeSlot);
        });
    }

    // Функция сброса выбора времени
    function resetTimeSelection() {
        document.querySelectorAll('.time-slot').forEach(el => el.classList.remove('selected'));
        startTime = null;
        endTime = null;
        resetTimeBtn.style.display = 'none';
        updateSummary();
    }

    // Обработчик кнопки сброса
    resetTimeBtn.addEventListener('click', function () {
        resetTimeSelection();
    });

    function updateSummary() {

        const availableTimesForDate = selectedDate
            ? availableDates[selectedDate] || []
            : [];



        if (selectedDate) {
            const date = new Date(selectedDate);
            document.querySelector('.summary-date').textContent =
                `${date.getDate()}.${date.getMonth() + 1}.${date.getFullYear()}`;
        }

        if (startTime && endTime) {
            const availableTimesForDate = availableDates[selectedDate] || [];
            const startIndex = availableTimesForDate.indexOf(startTime);
            const endIndex = availableTimesForDate.indexOf(endTime);
            const hours = endIndex - startIndex + 1;

            document.querySelector('.summary-time').textContent = `${startTime} - ${endTime}`;
            document.querySelector('.summary-price').textContent = hours * pricePerHour;
        } else if (startTime) {
            document.querySelector('.summary-time').textContent = `${startTime} - (выберите время окончания)`;
            document.querySelector('.summary-price').textContent = '...';
        } else {
            document.querySelector('.summary-time').textContent = '(выберите время)';
            document.querySelector('.summary-price').textContent = '...';
        }
    }

    // Обработка формы
    document.getElementById('bookingForm').addEventListener('submit', function (e) {
        e.preventDefault();

        if (!selectedDate || !startTime || !endTime) {
            alert('Пожалуйста, выберите дату и время бронирования');
            return;
        }

        // console.log('userId =', window.userId);
        // console.log(selectedDate, startTime, endTime)
        // const isAuthorized = !!window.userId;
        // console.log('isAuthorized =', isAuthorized);
        // if (!isAuthorized) {
        //     alert("Необходима регистрация");
        //     return;
        // }

        const formData = {
            date: selectedDate,
            startTime: startTime,
            endTime: endTime,
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
                alert(`Студия успешно забронирована на ${selectedDate} с ${startTime} до ${endTime}`);
            })
            .catch(error => {
                alert("Ошибка при бронировании.");
                console.error(error);
            });
    });

    // Запускаем календарь
    initCalendar();
});

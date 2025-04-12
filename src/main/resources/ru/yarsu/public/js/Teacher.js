// teacher-calendar.js
document.addEventListener('DOMContentLoaded', function() {
    // 1. Настройка данных
    const now = new Date();
    const currentYear = now.getFullYear();
    const currentMonth = now.getMonth();
    const currentDay = now.getDate();

    // Доступное время преподавателя (пример)
    const availableTimes = generateAvailableDates(currentYear, currentMonth, currentDay);
    
    // Выходные дни (суббота и воскресенье)
    const blockedDates = getWeekendDates(currentYear, currentMonth, currentDay);

    // 2. Получаем элементы DOM
    const calendarDays = document.getElementById('calendarDays');
    const currentMonthYear = document.getElementById('currentMonthYear');
    const prevMonthBtn = document.getElementById('prevMonth');
    const nextMonthBtn = document.getElementById('nextMonth');
    const timeSlotsContainer = document.querySelector('.time-slots-container');
    const timeSlots = document.getElementById('timeSlots');
    const bookingSummary = document.querySelector('.booking-summary');
    const resetTimeBtn = document.querySelector('.reset-time');
    const summaryDate = document.getElementById('summaryDate');
    const summaryTime = document.getElementById('summaryTime');
    const bookingForm = document.getElementById('bookingForm');

    // 3. Инициализация переменных
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    let currentDate = new Date();
    currentDate.setHours(0, 0, 0, 0);
    let selectedDate = null;
    let selectedTime = null;

    // 4. Основные функции

    // Генерация доступных дат (на 2 месяца вперед)
    function generateAvailableDates(year, month, startDay) {
        const available = {};
        const daysInMonth = new Date(year, month + 2, 0).getDate();
        const endDay = Math.min(startDay + 60, daysInMonth); // 60 дней = ~2 месяца
        
        // Добавляем рабочие дни (пн-пт) с интервалом в 2-3 дня
        for (let day = startDay; day <= endDay; day++) {
            const date = new Date(year, month, day);
            if (date.getDay() !== 0 && date.getDay() !== 6) { // Не выходные
                if (day % 3 === 0 || day % 4 === 0) { // Примерное расписание
                    available[formatDate(date)] = generateTimeSlotsForDay();
                }
            }
        }
        return available;
    }

    // Генерация временных слотов для дня
    function generateTimeSlotsForDay() {
        const slots = [];
        const startHour = 10; // Начало рабочего дня
        const endHour = 19;   // Конец рабочего дня
        const lessonDuration = 1; // Длительность занятия (часы)
        
        // Создаем слоты каждый час
        for (let hour = startHour; hour <= endHour - lessonDuration; hour++) {
            // 70% вероятности что слот будет доступен
            if (Math.random() > 0.3) {
                slots.push(`${hour}:00`);
            }
        }
        return slots;
    }

    // Получение дат выходных
    function getWeekendDates(year, month, startDay) {
        const weekends = [];
        const daysInMonth = new Date(year, month + 2, 0).getDate();
        const endDay = Math.min(startDay + 60, daysInMonth);
        
        for (let day = startDay; day <= endDay; day++) {
            const date = new Date(year, month, day);
            if (date.getDay() === 0 || date.getDay() === 6) {
                weekends.push(formatDate(date));
            }
        }
        return weekends;
    }

    // Форматирование даты в YYYY-MM-DD
    function formatDate(date) {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
    }

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
            calendarDays.appendChild(createDayElement('other-month'));
        }
        
        // Дни текущего месяца
        for (let i = 1; i <= daysInMonth; i++) {
            const dayDate = new Date(year, month, i);
            dayDate.setHours(0, 0, 0, 0);
            const dateStr = formatDate(dayDate);
            const isBlocked = blockedDates.includes(dateStr);
            const isPast = dayDate < today;
            const hasAvailableTimes = availableTimes[dateStr] && availableTimes[dateStr].length > 0;
            
            const dayElement = createDayElement(
                isPast ? 'past' : 
                isBlocked ? 'unavailable' : 
                hasAvailableTimes ? 'available' : 'unavailable',
                i
            );
            
            if (!isPast && !isBlocked && hasAvailableTimes) {
                dayElement.addEventListener('click', () => selectDate(dayElement, dateStr));
            }
            
            calendarDays.appendChild(dayElement);
        }
        
        // Пустые ячейки следующего месяца
        const totalCells = Math.ceil((firstDayOfWeek + daysInMonth) / 7) * 7;
        const remainingCells = totalCells - (firstDayOfWeek + daysInMonth);
        
        for (let i = 0; i < remainingCells; i++) {
            calendarDays.appendChild(createDayElement('other-month'));
        }
        
        // Блокировка кнопок навигации
        const prevMonth = new Date(year, month - 1, 1);
        const isPrevMonthPast = prevMonth < new Date(today.getFullYear(), today.getMonth(), 1);
        prevMonthBtn.classList.toggle('disabled', isPrevMonthPast);
        
        const maxAllowedDate = new Date();
        maxAllowedDate.setMonth(today.getMonth() + 2); // 2 месяца вперед
        const nextMonth = new Date(year, month + 1, 1);
        const isNextMonthTooFar = nextMonth > maxAllowedDate;
        nextMonthBtn.classList.toggle('disabled', isNextMonthTooFar);
    }

    // Создание элемента дня
    function createDayElement(className, content = '') {
        const dayElement = document.createElement('div');
        dayElement.className = `calendar-day ${className}`;
        dayElement.textContent = content;
        return dayElement;
    }

    // Выбор даты
    function selectDate(element, dateStr) {
        document.querySelectorAll('.calendar-day').forEach(el => el.classList.remove('selected'));
        element.classList.add('selected');
        selectedDate = dateStr;
        
        timeSlotsContainer.style.display = 'block';
        bookingSummary.style.display = 'block';
        resetTimeBtn.style.display = 'none';
        
        updateSummary();
        renderTimeSlots(dateStr);
    }

    // Отрисовка временных слотов
    function renderTimeSlots(dateStr) {
        timeSlots.innerHTML = '';
        selectedTime = null;
        
        const timesForDate = availableTimes[dateStr] || [];
        
        timesForDate.forEach(time => {
            const timeSlot = document.createElement('div');
            timeSlot.className = 'time-slot available';
            timeSlot.textContent = time;
            
            timeSlot.addEventListener('click', () => selectTime(timeSlot, time));
            
            timeSlots.appendChild(timeSlot);
        });
    }

    // Выбор времени
    function selectTime(element, time) {
        if (selectedTime === time) {
            resetTimeSelection();
            return;
        }
        
        document.querySelectorAll('.time-slot').forEach(el => el.classList.remove('selected'));
        element.classList.add('selected');
        selectedTime = time;
        resetTimeBtn.style.display = 'block';
        updateSummary();
    }

    // Сброс выбора времени
    function resetTimeSelection() {
        document.querySelectorAll('.time-slot').forEach(el => el.classList.remove('selected'));
        selectedTime = null;
        resetTimeBtn.style.display = 'none';
        updateSummary();
    }

    // Обновление сводки
    function updateSummary() {
        summaryDate.textContent = selectedDate 
            ? formatSelectedDate(selectedDate) 
            : 'не выбрано';
        
        summaryTime.textContent = selectedTime || 'не выбрано';
    }

    // Форматирование выбранной даты
    function formatSelectedDate(dateStr) {
        const date = new Date(dateStr);
        return date.toLocaleDateString('ru-RU', {
            day: 'numeric',
            month: 'numeric',
            year: 'numeric'
        });
    }

    // Обработка формы
    function handleFormSubmit(e) {
        e.preventDefault();
        
        if (!selectedDate || !selectedTime) {
            alert('Пожалуйста, выберите дату и время занятия');
            return;
        }
        
        // Здесь должна быть отправка данных на сервер
        const formData = {
            name: document.getElementById('name').value,
            phone: document.getElementById('phone').value,
            date: selectedDate,
            time: selectedTime,
            instrument: document.getElementById('instrument').value
        };
        
        console.log('Данные для отправки:', formData);
        alert(`Заявка отправлена! Вы записаны на ${formatSelectedDate(selectedDate)} в ${selectedTime}`);
    }

    // 5. Инициализация
    resetTimeBtn.addEventListener('click', resetTimeSelection);
    bookingForm.addEventListener('submit', handleFormSubmit);
    initCalendar();

    console.log('Teacher calendar initialized');
});
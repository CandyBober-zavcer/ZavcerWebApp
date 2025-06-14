document.addEventListener('DOMContentLoaded', function () {

    const availableDates = JSON.parse(json);
    const bookedDates = {};


    const calendarDays = document.getElementById('calendarDays');
    const currentMonthYear = document.getElementById('currentMonthYear');
    const prevMonthBtn = document.getElementById('prevMonth');
    const nextMonthBtn = document.getElementById('nextMonth');
    const timePicker = document.querySelector('.time-picker');
    const timeSlots = document.querySelector('.time-slots');
    const bookingSummary = document.querySelector('.booking-summary');
    const resetTimeBtn = document.querySelector('.reset-time');


    const today = new Date();
    today.setHours(0, 0, 0, 0);

    let currentDate = new Date();
    currentDate.setHours(0, 0, 0, 0);
    let selectedDate = null;
    let startTime = null;
    let endTime = null;


    function addOneHour(timeStr) {
        let [hours, minutes] = timeStr.split(':').map(Number);
        hours = (hours + 1) % 24;

        let hoursStr = hours.toString().padStart(2, '0');
        return `${hoursStr}:${minutes.toString().padStart(2, '0')}`;
    }




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

        for (let i = 0; i < firstDayOfWeek; i++) {
            const dayElement = document.createElement('div');
            dayElement.className = 'calendar-day other-month';
            calendarDays.appendChild(dayElement);
        }

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

                    timePicker.style.display = 'block';
                    bookingSummary.style.display = 'none';
                    resetTimeBtn.style.display = 'none';

                    updateSummary();


                    generateTimeSlots();
                });
            }

            calendarDays.appendChild(dayElement);
        }

        const totalCells = Math.ceil((firstDayOfWeek + daysInMonth) / 7) * 7;
        const remainingCells = totalCells - (firstDayOfWeek + daysInMonth);

        for (let i = 0; i < remainingCells; i++) {
            const dayElement = document.createElement('div');
            dayElement.className = 'calendar-day other-month';
            calendarDays.appendChild(dayElement);
        }

        const prevMonth = new Date(year, month - 1, 1);
        const isPrevMonthPast = prevMonth < new Date(today.getFullYear(), today.getMonth(), 1);

        if (isPrevMonthPast) {
            prevMonthBtn.classList.add('disabled');
        } else {
            prevMonthBtn.classList.remove('disabled');
        }


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

    function formatDate(date) {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
    }


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
                if (startTime && endTime) {
                    resetTimeSelection();
                    return;
                }

                if (startTime === time && !endTime) {
                    resetTimeSelection();
                    return;
                }

                if (!startTime) {
                    document.querySelectorAll('.time-slot').forEach(el => el.classList.remove('selected'));
                    this.classList.add('selected');
                    startTime = time;
                    resetTimeBtn.style.display = 'block';

                    bookingSummary.style.display = 'block';
                } else {

                    const startIndex = availableTimesForDate.indexOf(startTime);
                    const endIndex = availableTimesForDate.indexOf(time);

                    if (endIndex > startIndex) {
                        endTime = time;


                        document.querySelectorAll('.time-slot').forEach(el => el.classList.remove('selected'));
                        for (let i = startIndex; i <= endIndex; i++) {
                            document.querySelectorAll('.time-slot')[i].classList.add('selected');
                        }
                    } else {

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


    function resetTimeSelection() {
        document.querySelectorAll('.time-slot').forEach(el => el.classList.remove('selected'));
        startTime = null;
        endTime = null;
        resetTimeBtn.style.display = 'none';
        updateSummary();
    }

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

            document.querySelector('.summary-time').textContent = `${startTime} - ${addOneHour(endTime)}`;
            document.querySelector('.summary-price').textContent = hours * pricePerHour;
        } else if (startTime) {
            document.querySelector('.summary-time').textContent = `${startTime} - (выберите время окончания)`;
            document.querySelector('.summary-price').textContent = '...';
        } else {
            document.querySelector('.summary-time').textContent = '(выберите время)';
            document.querySelector('.summary-price').textContent = '...';
        }
    }

    document.getElementById('bookingForm').addEventListener('submit', function (e) {
        e.preventDefault();

        if (!selectedDate || !startTime || !endTime) {
            alert('Пожалуйста, выберите дату и время бронирования');
            return;
        }


        const formData = {
            date: selectedDate,
            startTime: startTime,
            endTime: endTime,
        };
        console.log('Отправляемые данные:', formData);
        fetch('/spots/1', {
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

    initCalendar();
});

function parseToValidDate(date) {
    const year = new Intl.DateTimeFormat('en', {year: "numeric"}).format(date);
    const month = new Intl.DateTimeFormat('en', {month: "2-digit"}).format(date);
    const day = new Intl.DateTimeFormat('en', {day: "2-digit"}).format(date);
    let hour = new Intl.DateTimeFormat('ru', {hour: '2-digit'}).format(date);
    if (Number.parseInt(hour) < 10) hour = `0${hour}`;
    let minute = new Intl.DateTimeFormat('ru', {minute: '2-digit'}).format(date);
    if (Number.parseInt(minute) < 10) minute = `0${minute}`;
    return `${year}-${month}-${day} ${hour}:${minute}`;
}

function showErrBlock(text) {
    const infoBlock = $('#errBlock');
    infoBlock.html(text).fadeIn().delay(600).fadeOut(600, function () {
        infoBlock.removeAttr('style');
    });
}
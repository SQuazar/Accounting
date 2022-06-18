function parseToValidDate(date) {
    const year = new Intl.DateTimeFormat('en', {year: "numeric"}).format(date);
    const month = new Intl.DateTimeFormat('en', {month: "2-digit"}).format(date);
    const day = new Intl.DateTimeFormat('en', {day: "2-digit"}).format(date);
    let hour = new Intl.DateTimeFormat('ru', {hour: '2-digit'}).format(date);
    let minute = new Intl.DateTimeFormat('ru', {minute: '2-digit'}).format(date);
    if (Number.parseInt(minute) < 10) minute = `0${minute}`;
    return `${year}-${month}-${day} ${hour}:${minute}`;
}

function parseFromValidDate(date) {
    //2022-06-18 01:58
    //yyyy-MM-ddThh:mm
    const dateArr = date.toString().split(/[- :]+/)
    return `${dateArr[0]}-${dateArr[1]}-${dateArr[2]}T${dateArr[3]}:${dateArr[4]}`
}

function showErrBlock(text) {
    const infoBlock = $('#errBlock');
    infoBlock.html(text).fadeIn().delay(600).fadeOut(600, function () {
        infoBlock.removeAttr('style');
    });
}

function getParamFromURL(key) {
    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    return urlParams.get(key);
}

function setUrlParams(value) {
    window.history.pushState({}, document.title, "/" + value );
}
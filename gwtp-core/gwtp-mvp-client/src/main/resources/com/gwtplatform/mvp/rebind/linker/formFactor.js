function findFormFactorFromQuery(href, propertyName) {
    if (propertyName == '') {
        return undefined;
    }
    var start = href.indexOf(propertyName);
    if (start >= 0) {
        var value = href.substring(start);
        var begin = value.indexOf("=") + 1;
        var end = value.indexOf("&");
        if (end == -1) {
            end = value.indexOf("#");
            if (end == -1) {
                end = value.length;
            }
        }
        value = value.substring(begin, end);
        if (value != '') {
            return value;
        }
    }

    return undefined;
}

function findFormFactorFromUserAgent(userAgent) {
    var mobileRe = /(iPhone|iPod|Mobi|Mini|Fennec|HTC|j2me|J2ME|Symbian|Puffin|portalmmm|PSP|PalmOS|DoCoMo|webOS|MMP|MIDP|UCBrowser|WindowsCE|Novarra-Vision|BOLT|SonyEricsson|Android 0|UP\.Link)/;
    var notMobileRe = /(Silk|iPad|GT-P1000M|Xoom|SCH-I800)/;
    var tabletRe = /(Android|iPad|[tT]ablet|Kindle|nook)/;

    if (userAgent.match(mobileRe) && !userAgent.match(notMobileRe)) {
        return "mobile";
    } else if (userAgent.match(tabletRe)) {
        return "tablet";
    }
    return "desktop";
}

function findFormFactor(propertyName, location, navigator) {
    var formFactor = findFormFactorFromQuery(location.href, propertyName);

    if (formFactor == undefined) {
        formFactor = findFormFactorFromUserAgent(navigator.userAgent);
    }

    return formFactor;
}

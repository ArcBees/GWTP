function findFormFactorFromQuery(href, propertyName) {
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
        return value.substring(begin, end);
    }

    return undefined;
}

function findFormFactorFromUserAgent(userAgent) {
    var mobileRe = /(iphone|ipod|mobile)/i;
    var notMobileRe = /kindle/i;
    var tabletRe = /(android|ipad|tablet|kindle|silk|touch)/i;
    var notTabletRe = /desktop/i;

    if (userAgent.match(mobileRe) && !userAgent.match(notMobileRe)) {
        return "mobile";
    } else if (userAgent.match(tabletRe) && !userAgent.match(notTabletRe)) {
        return "tablet";
    }
    return "desktop";
}

function findFormFactor(propertyName) {
    var formFactor = findFormFactorFromQuery(location.href, propertyName);

    if (formFactor == undefined) {
        formFactor = findFormFactorFromUserAgent(navigator.userAgent);
    }

    return formFactor;
}

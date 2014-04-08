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
            if (["mobile","tablet","desktop"].indexOf(value) > -1) {
                return value;
            } else {
                window.console && console.log(value + " is not a valid formfactor and has been ignored.");
            }
        }
    }

    return undefined;
}

function findFormFactorFromUserAgent(userAgent) {
    var mobileRe = /(Mobi|DoCoMo|Mini|Puffin|Fennec|j2me|Palm|portalmmm|Symbian ?OS|BOLT|webOS|UP\.(Link|Browser)|MIDP|PSP|WP|SonyEricsson|Windows ?CE|Android 0|Novarra-Vision|Nokia|Vodafone|uZardWeb)/;
    var notMobileRe = /(Silk-Accelerated|Pad|GT-P1000M|Xoom|SCH-I800)/;
    var tabletRe = /(Pad|Android|Kindle|Silk-Accelerated|nook|PlayBook)/;

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

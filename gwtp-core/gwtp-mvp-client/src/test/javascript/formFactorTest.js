try {

  function testUserAgent(userAgent, formfactor) {
    it("should be " + formfactor + " when userAgent is " + userAgent, function() {
      expect(findFormFactorFromUserAgent(userAgent)).toBe(formfactor);
    });
  }

  function checkUserAgentArrayFormfactor (uaArray, formfactor) {
    describe("for a " + formfactor + " device", function() {
      for (var i = 0; i < uaArray.length; i++) {
        testUserAgent(uaArray[i], formfactor);
      }
    });
  }

  describe('finding from user agent', function() {
    checkUserAgentArrayFormfactor(window.desktopUserAgents, "desktop");
    checkUserAgentArrayFormfactor(window.mobileUserAgents, "mobile");
    checkUserAgentArrayFormfactor(window.tabletUserAgents, "tablet");
  });
  
  describe('finding form factor from query parameter', function() {
    it('should return undefined when the property is not in the url', function() {
      return expect(findFormFactorFromQuery('/testurl#potato=mobile', 'formFactor')).toBe(void 0);
    });
    it('should return undefined when the property is an empty string', function() {
      return expect(findFormFactorFromQuery('/', '')).toBe(void 0);
    });
    it('should return undefined when the property is not provided', function() {
      return expect(findFormFactorFromQuery('/')).toBe(void 0);
    });
    return describe('when a property is specified', function() {
      it('should return the form factor specified by the property', function() {
        return expect(findFormFactorFromQuery('/testurl#aProperty=aValue', 'aProperty')).toBe('aValue');
      });
      it('should return undefined when the value is not defined', function() {
        return expect(findFormFactorFromQuery('testurl#aProperty=', 'aProperty')).toBe(void 0);
      });
      it('should mark the end of the value with the token #', function() {
        return expect(findFormFactorFromQuery('testurl#aProperty=aValue#anotherValue', 'aProperty')).toBe('aValue');
      });
      return it('should mark the end of the value with the token &', function() {
        return expect(findFormFactorFromQuery('testurl#aProperty=aValue&anotherValue', 'aProperty')).toBe('aValue');
      });
    });
  });

  describe('finding form factor', function() {
    var location, navigator;
    it('should return the value from the url if there was one provided', function() {});
    location = {
      href: '/formFactor=mobile'
    };
    expect(findFormFactor('formFactor', location, navigator)).toBe('mobile');
    it('should fallback to user agent if property is not found in URL', function() {});
    location = {
      href: '/someUrl'
    };
    navigator = {
      userAgent: 'Mozilla/5.0 (iPad; U; CPU OS 3_2 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Version/4.0.4 Mobile/7B334b Safari/531.21.10'
    };
    return expect(findFormFactor('formFactor', location, navigator)).toBe('tablet');
  });

} catch (e) {
  describe("a syntax error in formFactorTest.js", function() {
    it ("should cause the build to fail", function() {
      expect(true).toBeFalsy();
    });
  });
}
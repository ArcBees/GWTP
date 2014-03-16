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
      expect(findFormFactorFromQuery('/testurl#potato=mobile', 'formFactor')).toBeUndefined();
    });
    it('should return undefined when the property is an empty string', function() {
      expect(findFormFactorFromQuery('/', '')).toBeUndefined();
    });
    it('should return undefined when the property is not provided', function() {
      expect(findFormFactorFromQuery('/')).toBeUndefined();
    });
    describe('when a property is specified', function() {
      it('should return mobile when mobile is specified by the property', function() {
        expect(findFormFactorFromQuery('/testurl#aProperty=mobile', 'aProperty')).toBe('mobile');
      });
      it('should return tablet when tablet is specified by the property', function() {
        expect(findFormFactorFromQuery('/testurl#aProperty=tablet', 'aProperty')).toBe('tablet');
      });
      it('should return desktop when desktop is specified by the property', function() {
        expect(findFormFactorFromQuery('/testurl#aProperty=desktop', 'aProperty')).toBe('desktop');
      });
      it('should return undefined when the value is not defined', function() {
        expect(findFormFactorFromQuery('testurl#aProperty=', 'aProperty')).toBeUndefined();
      });
      it('should mark the end of the value with the token #', function() {
        expect(findFormFactorFromQuery('testurl#aProperty=mobile#anotherValue', 'aProperty')).toBe('mobile');
      });
      it('should mark the end of the value with the token &', function() {
        expect(findFormFactorFromQuery('testurl#aProperty=mobile&tablet', 'aProperty')).toBe('mobile');
      });
      it('should return undefined when the value is not mobile, tablet or desktop', function() {
        expect(findFormFactorFromQuery('testurl#aProperty=aValue', 'aProperty')).toBeUndefined();
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
  describe("an error in formFactorTest.js", function() {
    it ("should cause the build to fail", function() {
      expect(true).toBeFalsy();
    });
  });
}

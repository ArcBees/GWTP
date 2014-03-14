describe 'finding from user agent', ->
  describe 'for a mobile device', ->
    for userAgent of window.mobileUserAgents ->
      it 'should be mobile when userAgent is ' + userAgent, ->
        expect(findFormFactorFromUserAgent(userAgent)).toBe 'mobile'
      
  describe 'for tablet', ->
    for userAgent of window.tabletUserAgents ->
      it 'should be tablet when userAgent is ' + userAgent, ->
        expect(findFormFactorFromUserAgent(userAgent)).toBe 'tablet'

  describe 'for a desktop computer', ->
     for userAgent of window.desktopUserAgents ->
      it 'should be desktop when userAgent is ' + userAgent, ->
        expect(findFormFactorFromUserAgent(userAgent)).toBe 'desktop'

describe 'finding form factor from query parameter', ->
  it 'should return undefined when the property is not in the url', ->
    expect(findFormFactorFromQuery('/testurl#potato=mobile', 'formFactor')).toBe undefined

  it 'should return undefined when the property is an empty string', ->
    expect(findFormFactorFromQuery('/', '')).toBe undefined

  it 'should return undefined when the property is not provided', ->
    expect(findFormFactorFromQuery('/')).toBe undefined

  describe 'when a property is specified', ->
    it 'should return the form factor specified by the property', ->
      expect(findFormFactorFromQuery('/testurl#aProperty=aValue', 'aProperty')).toBe 'aValue'

    it 'should return undefined when the value is not defined', ->
      expect(findFormFactorFromQuery('testurl#aProperty=', 'aProperty')).toBe undefined

    it 'should mark the end of the value with the token #', ->
      expect(findFormFactorFromQuery('testurl#aProperty=aValue#anotherValue', 'aProperty')).toBe 'aValue'

    it 'should mark the end of the value with the token &', ->
      expect(findFormFactorFromQuery('testurl#aProperty=aValue&anotherValue', 'aProperty')).toBe 'aValue'

describe 'finding form factor', ->
  it 'should return the value from the url if there was one provided', ->
  location =
    href: '/formFactor=mobile'
  expect(findFormFactor('formFactor', location, navigator)).toBe 'mobile'

  it 'should fallback to user agent if property is not found in URL', ->
  location =
   href: '/someUrl' # No property specified
  navigator =
    userAgent: 'mobile'
  expect(findFormFactor('formFactor', location, navigator)).toBe 'mobile'

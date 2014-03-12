describe 'finding from user agent', ->
  describe 'for a mobile device', ->
    it 'should be mobile when userAgent is iphone', ->
      expect(findFormFactorFromUserAgent('iphone')).toBe 'mobile'

    it 'should be mobile when userAgent is ipod', ->
      expect(findFormFactorFromUserAgent('ipod')).toBe 'mobile'

    it 'should be mobile when userAgent is mobile', ->
      expect(findFormFactorFromUserAgent('mobile')).toBe 'mobile'

  describe 'for tablet', ->
    it 'should be tablet when userAgent is android', ->
      expect(findFormFactorFromUserAgent('android')).toBe 'tablet'

    it 'should be tablet when userAgent is tablet', ->
      expect(findFormFactorFromUserAgent('tablet')).toBe 'tablet'

    it 'should be tablet when userAgent is ipad', ->
      expect(findFormFactorFromUserAgent('ipad')).toBe 'tablet'

    it 'should be tablet when userAgent is silk', ->
      expect(findFormFactorFromUserAgent('silk')).toBe 'tablet'

    it 'should be tablet when userAgent is touch', ->
      expect(findFormFactorFromUserAgent('touch')).toBe 'tablet'

  describe 'for a desktop computer', ->
    it 'should be desktop when userAgent is desktop', ->
      expect(findFormFactorFromUserAgent('desktop')).toBe 'desktop'

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

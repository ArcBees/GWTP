desktopUserAgents =
  [
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1664.3 Safari/537.36",
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 1083) AppleWebKit/537.36 (KHTML like Gecko) Chrome/28.0.1469.0 Safari/537.36"
  ]

tabletUserAgents =
  [
    "Mozilla/5.0 (iPad; U; CPU OS 3_2 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Version/4.0.4 Mobile/7B334b Safari/531.21.10",
    "Mozilla/5.0 (iPad; U; CPU OS 4_2_1 like Mac OS X; ja-jp) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8C148 Safari/6533.18.5"
  ]

mobileUserAgents =
  [
    "Mozilla/5.0 (iPod; U; CPU iPhone OS 3_1_1 like Mac OS X; en-us) AppleWebKit/528.18 (KHTML, like Gecko) Mobile/7C145",
    "Mozilla/5.0 (iPod; U; CPU iPhone OS 2_2_1 like Mac OS X; en-us) AppleWebKit/525.18.1 (KHTML, like Gecko) Version/3.1.1 Mobile/5H11a Safari/525.20"
  ]

describe 'finding from user agent', ->
  describe 'for a mobile device', ->
    for userAgent of mobileUserAgents ->
      it 'should be mobile when userAgent is ' + userAgent, ->
        expect(findFormFactorFromUserAgent(userAgent)).toBe 'mobile'
      
  describe 'for tablet', ->
    for userAgent of tabletUserAgents ->
      it 'should be tablet when userAgent is ' + userAgent, ->
        expect(findFormFactorFromUserAgent(userAgent)).toBe 'tablet'

  describe 'for a desktop computer', ->
     for userAgent of desktopUserAgents ->
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

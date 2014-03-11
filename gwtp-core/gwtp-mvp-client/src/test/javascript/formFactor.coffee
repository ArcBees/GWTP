describe 'finding mobile form factor from user agent', ->
  it 'should be mobile when userAgent is iphone', ->
    expect(findFormFactorFromUserAgent('iphone')).toBe 'mobile'

  it 'should be mobile when userAgent is ipod', ->
    expect(findFormFactorFromUserAgent('ipod')).toBe 'mobile'

  it 'should be mobile when userAgent is mobile', ->
    expect(findFormFactorFromUserAgent('mobile')).toBe 'mobile'

describe 'finding tablet form factor from user agent', ->
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

describe 'finding desktop form factor from user agent', ->
  it 'should be desktop when userAgent is desktop', ->
    expect(findFormFactorFromUserAgent('desktop')).toBe 'desktop'
describe 'after all tests have run', ->
  it 'should have set window.formfactorTestsHaveRun to true', ->
    expect(window.formfactorTestsHaveRun).toBeTruthy()
  it 'should have defined window.mobileUserAgents', ->
    expect(window.mobileUserAgents).toBeDefined()
  it 'should have defined window.desktopUserAgents', ->
    expect(window.desktopUserAgents).toBeDefined()
  it 'should have defined window.tabletUserAgents', ->
    expect(window.tabletUserAgents).toBeDefined()
GameObject = dofile('src/main/lua/org.darkmentat.LandOfLords.Server.scripts/GameObject.lua')

local unit = GameObject.create()
GameObject.makeObservational(unit)
GameObject.makeMovable(unit, 0, 0)
unit.Controller:stay()
return unit

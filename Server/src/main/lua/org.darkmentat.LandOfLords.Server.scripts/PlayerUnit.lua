Skills = dofile('src/main/lua/org.darkmentat.LandOfLords.Server.scripts/Skills.lua')
GameObject = dofile('src/main/lua/org.darkmentat.LandOfLords.Server.scripts/GameObject.lua')

function PlayerUnit()
    local unit = GameObject.create()
    unit.Skills.Moving = Skills.Moving

    unit.X = 0
    unit.Y = 0

    unit.State = unit.Skills.Moving.PossibleStates.Moving(unit, 10, 10)

    return unit
end

return PlayerUnit()
Skills = dofile('src/main/lua/org.darkmentat.LandOfLords.Server.scripts/Skills.lua')

local GameObject = {}

GameObject.create = function ()
    local ctor = {}

    ctor.Items = {}
    ctor.Skills = {}
    ctor.State = {}
    ctor.Memory = {}
    ctor.Behaviour = nil

    return ctor
end

GameObject.makePositionable = function (go, x, y)
    go.X = x
    go.Y = y

    return go
end

GameObject.makeMovable = function (go, x, y)
    go = GameObject.makePositionable(go, x, y)

    go.Skills.Moving = Skills.Moving

    function go:move(toX, toY)
        self.State = self.Skills.Moving.PossibleStates.Moving(self, toX, toY)
    end
    function go:stay()
        self.State = self.Skills.Moving.PossibleStates.Staying(self)
    end

    return go
end

return GameObject
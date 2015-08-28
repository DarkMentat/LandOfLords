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
    GameObject.makePositionable(go, x, y)

    go.Skills.Moving = Skills.Moving(1)

    function go:move(toX, toY)
        self.State = self.Skills.Moving.PossibleStates.Moving(self, toX, toY)
    end
    function go:stay()
        self.State = self.Skills.Moving.PossibleStates.Staying(self)
    end

    return go
end

GameObject.makeObservational = function (go)
    go.Surroundings = {}
    go.Skills.Observation = Skills.Observation(1)

    function go:getSurroundingRadius()
        return self.Skills.Observation.Mastery
    end

    function go:setSurroundings(array)
        self.Surroundings = {}

        for _,v in pairs(array) do
            table.insert(self.Surroundings, {
                X = v.X,
                Y = v.Y,
                Description = v.Description,
                Positionables = v.Positionables
            })
        end
   end
end

return GameObject
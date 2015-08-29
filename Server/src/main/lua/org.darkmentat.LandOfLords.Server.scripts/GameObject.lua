Skills = dofile('src/main/lua/org.darkmentat.LandOfLords.Server.scripts/Skills.lua')

local GameObject = {}

GameObject.create = function ()
    local ctor = {}

    ctor.Items = {}
    ctor.Skills = {}
    ctor.State = {}
    ctor.Memory = {}
    ctor.Behaviour = nil
    ctor.Controller = {}

    function ctor:eval(untrusted_code)
        local untrusted_function, message = load(untrusted_code, nil, 't', self.Controller)
        if not untrusted_function then return nil, message end
        return pcall(untrusted_function)
    end

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

    function go.Controller.move(toX, toY)
        go.State = go.Skills.Moving.PossibleStates.Moving(go, toX, toY)
    end
    function go.Controller.stay()
        go.State = go.Skills.Moving.PossibleStates.Staying(go)
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
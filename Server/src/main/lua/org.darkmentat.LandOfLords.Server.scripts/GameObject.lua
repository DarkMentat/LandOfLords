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

return GameObject
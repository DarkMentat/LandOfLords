function GameObject()
    local ctor = {}

    ctor.Items = {}
    ctor.Skills = {}
    ctor.State = {}
    ctor.Memory = {}
    ctor.Behaviour = nil

    return ctor
end

GeneralSkills = {
    Moving = {
        Id = 1,

        PossibleStates = {
            Staying = function (owner, x, y)
                local state = {}

                state.Owner = owner

                state.GameObjectState = 'STAYING'
                state.X = x
                state.Y = y

                state.getStateValues = function(self)
                    return {
                        X = self.X,
                        Y = self.Y
                    }
                end

                return state
            end,
            Moving = function (owner, x, y, dirX, dirY)
                local state = {}

                state.Owner = owner

                state.GameObjectState = 'MOVING'
                state.X = x
                state.Y = y
                state.DirectionX = dirX
                state.DirectionY = dirY

                state.move = function(self)
                    local speed = 1;

                    local len = math.sqrt((self.DirectionX-self.X)^2 + (self.DirectionY-self.Y)^2)
                    local dx = (self.DirectionX-self.X)/len
                    local dy = (self.DirectionY-self.Y)/len

                    self.X = self.X + dx*speed
                    self.Y = self.Y + dy*speed

                    if len < 2 then
                        self.Owner.State = self.Owner.Skills[1].PossibleStates.Staying(self.Owner, self.X, self.Y)
                    end
                end

                state.getStateValues = function(self)
                    return {
                        X = self.X,
                        Y = self.Y,
                        DirectionX = self.DirectionX,
                        DirectionY = self.DirectionY
                    }
                end

                return state
            end
        }
    }
}

function PlayerUnit()
    local unit = GameObject()
    unit.Skills[1] = GeneralSkills.Moving
    -- unit.State = unit.Skills[1].PossibleStates.Staying(0,0)
    unit.State = unit.Skills[1].PossibleStates.Moving(unit, 0,0, 10, 10)

    return unit
end

return PlayerUnit()
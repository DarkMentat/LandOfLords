function createState(owner, name)
    return {
        Owner = owner,
        GameObjectState = name,

        getStateValues = function() return {} end
    }
end

local Skills = {}

Skills.Moving = function(mastery)
    return {
        Id = 1,
        Mastery = mastery,

        PossibleStates = {
            Staying = function (owner)
                local state = createState(owner, 'STAYING')

                return state
            end,
            Moving = function (owner, dirX, dirY)
                local state = createState(owner, 'MOVING')
                state.DirectionX = dirX
                state.DirectionY = dirY

                function state:performMoving()
                    local speed = self.Owner.Skills.Moving.Mastery;

                    local len = math.sqrt((self.DirectionX-self.Owner.X)^2 + (self.DirectionY-self.Owner.Y)^2)
                    local dx = (self.DirectionX-self.Owner.X)/len
                    local dy = (self.DirectionY-self.Owner.Y)/len

                    self.Owner.X = self.Owner.X + dx*speed
                    self.Owner.Y = self.Owner.Y + dy*speed

                    if len < 2 then
                        self.Owner.State = self.Owner.Skills.Moving.PossibleStates.Staying(self.Owner)
                    end
                end

                function state:getStateValues()
                    return {
                        DirectionX = self.DirectionX,
                        DirectionY = self.DirectionY
                    }
                end

                return state
            end
        }
    }
end

Skills.Observation = function(mastery)
    return {
        Id = 2,
        Mastery = mastery
    }
end

return Skills
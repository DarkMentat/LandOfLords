function createState(owner, name)
    return {
        Owner = owner,
        GameObjectState = name
    }
end

local Skills = {}

Skills.Moving = {
    Id = 1,

    PossibleStates = {
        Staying = function (owner)
            local state = createState(owner, 'STAYING')

            function state:getStateValues()
                return {}
            end

            return state
        end,
        Moving = function (owner, dirX, dirY)
            local state = createState(owner, 'MOVING')
            state.DirectionX = dirX
            state.DirectionY = dirY

            function state:move()
                local speed = 1;

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

return Skills
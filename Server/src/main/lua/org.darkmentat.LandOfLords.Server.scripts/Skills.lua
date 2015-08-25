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
        Staying = function (owner, x, y)
            local state = createState(owner, 'STAYING')
            state.X = x
            state.Y = y

            function state:getStateValues()
                return {
                    X = self.X,
                    Y = self.Y
                }
            end

            return state
        end,
        Moving = function (owner, x, y, dirX, dirY)
            local state = createState(owner, 'MOVING')
            state.X = x
            state.Y = y
            state.DirectionX = dirX
            state.DirectionY = dirY

            function state:move()
                local speed = 1;

                local len = math.sqrt((self.DirectionX-self.X)^2 + (self.DirectionY-self.Y)^2)
                local dx = (self.DirectionX-self.X)/len
                local dy = (self.DirectionY-self.Y)/len

                self.X = self.X + dx*speed
                self.Y = self.Y + dy*speed

                if len < 2 then
                    self.Owner.State = self.Owner.Skills.Moving.PossibleStates.Staying(self.Owner, self.X, self.Y)
                end
            end

            function state:getStateValues()
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

return Skills
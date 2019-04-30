class NexoLight:
    def __init__(self, name, controller):
        self.name = name
        self.controller = controller
        self.is_on = self.update()
        
    def turn_on(self):
        self.controller.set_state(self.name, 1)
        self.is_on = True
       
    def turn_off(self):
        self.controller.set_state(self.name, 0)
        self.is_on = False
        
    def is_on(self):
        return self.is_on
    
    def update(self):
        state = self.controller.get_state(self.name)
        return state

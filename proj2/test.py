import gym

import numpy as np 

env = gym.make("gym_zhed:zhed-v0")

q_table = np.zeros([68719476736, env.action_space.n])


#q_table = map()

#q_table = np.zeros([env.board.tobytes(), env.action_space.n])


#for i in range(40):
"""
print("obs_space ", env.observation_space)
print("obs_space.low ", env.observation_space.low)
print("obs_space.high ", env.observation_space.high)
print("obs_space.sample ", env.observation_space.sample())
"""

"""
while not env.done:
    if env.hasMovesLeft():
        env.step(env.action_space.sample())
        env.render()
      
    else:
        env.reset()
"""
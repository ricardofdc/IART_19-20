import gym

import numpy as np
from IPython.display import clear_output
from time import sleep
import random


def print_frames(frames):
    for i, frame in enumerate(frames):
        clear_output(wait=True)
        print()
        print(frame['frame'])
        print(f"Timestep: {i + 1}")
        print(f"State: {frame['state']}")
        print(f"Action: {frame['action']}")
        print(f"Reward: {frame['reward']}")
        sleep(.1)

filestr = "level"
level_number = input("Enter Level : ")
filestr += level_number
filestr += ".txt"

env = gym.make("gym_zhed:zhed-v0", filename=filestr)

q_table_file = "q_table"
q_table_file += level_number
q_table_file += ".txt"
try: q_table = np.genfromtxt(q_table_file, dtype=float)
except OSError:
    print("Q table does not exist, please train your agent first")
    exit()


### Test q_table

total_epochs, total_penalties = 0, 0
episodes = 200

for _ in range(episodes):
    epochs, penalties, reward = 0, 0, 0
    
    done = False
    
    while not done:
        state = env.encode()
        current_entry = []
        for entry in q_table:
            if entry[0] == state:
                current_entry = np.delete(entry,0,0)
                break
        action = np.argmax(current_entry)
        state, reward, done, info = env.step(action)

        if reward < 0:
            penalties += reward/10

        epochs += 1

        if not env.hasMovesLeft():
            env.reset()
    
    total_penalties += penalties
    total_epochs += epochs

print(f"Results after {episodes} episodes:")
print(f"Total timesteps: {total_epochs}")
print(f"Total penalties: {total_penalties}")
print(f"Average timesteps per episode: {total_epochs / episodes}")
print(f"Average penalties per episode: {total_penalties / episodes}")

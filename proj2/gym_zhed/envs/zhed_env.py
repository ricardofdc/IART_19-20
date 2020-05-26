import gym
from gym import error, spaces, utils
from gym.utils import seeding
import numpy as np
import matplotlib.pyplot as plt
import math 
import os

class ZhedEnv(gym.Env):
  metadata = {'render.modes': ['human']}

  def __init__(self, filename):
    path = os.getcwd()
    os.path.abspath(os.path.join(path, filename))
    self.filename = filename
    self.board = np.genfromtxt(self.filename, dtype =str)
    self.r = len(self.board)
    self.c = len(self.board[1,:])
    self.done = False
    self.pieces = self.getpieces()
    self.action_space = spaces.Discrete(4) #[0,1,2,3,4,5,6,7]
    self.possible_moves = self.action_space
    self.observation_space = spaces.Discrete(8*8) #spaces.Box(0,len(self.pieces))


  def step(self, action): #0-UP, 1-DOWN, 2-RIGHT, 3-LEFT
    pieceIndex = action // 4
    pieceMove = action % 4
    piece = self.pieces[pieceIndex]
    diffExpansion = self.moveSwitcher(pieceMove, piece)
    self.pieces = np.delete(self.pieces,piece,0)
    print(self.action_space)
    self.action_space = np.delete(self.action_space,[pieceIndex],0)
    self.action_space = np.delete(self.action_space,[pieceIndex+1],0)
    self.action_space = np.delete(self.action_space,[pieceIndex+2],0)
    self.action_space = np.delete(self.action_space,[pieceIndex+3],0)
    print(self.action_space)
    print(diffExpansion)

    #return observation, reward, done, {}

  def reset(self):
    self.__init__(self.filename)

  def render(self, mode='human'):
    print(np.matrix(self.board))

  def close(self):
    ...

  def addPieces(self):
    ...
  
  def calculateActionSpace(self):
    ...
    
  def getpieces(self):
    pieces = np.array([[0,0,0]])
    for i in range(0, self.r):
      for j in range(0, self.c):
          if self.board[i,j] != '.' and self.board[i,j] != 'W':
            piece = [[int(self.board[i,j]), int(i), int(j)]]
            pieces = np.append(pieces,piece, axis=0)
    pieces = np.delete(pieces,0,0)
    return pieces

  def moveSwitcher(self,argument,piece):
    print(argument)
    print(piece)
    switcher = {
      0: self.down,
      1: self.up,
      2: self.right,
      3: self.left
    }
    func = switcher.get(argument)
    return func(piece)
  
  def down(self,piece):
    print("down")
    numCellsExpanded = 0
    x = int(piece[2])
    y = int(piece[1])
    power = int(piece[0])
    self.board[y,x] = '#'
    for i in range(1,power+1):
      print(i)
      if y + i < self.r:
        if self.board[y+i,x] == '.':
          self.board[y+i,x] = '#'
          numCellsExpanded += 1
        elif self.board[y+i,x] == 'W':
          self.board[y+i,x] = '#'
          numCellsExpanded += 1
          self.done = True
          return numCellsExpanded - power
        else: 
          numCellsExpanded += 1
          i -= 1
    return numCellsExpanded - power

  def up(self,piece):
    print("up")
    numCellsExpanded = 0
    x = int(piece[2])
    y = int(piece[1])
    power = int(piece[0])
    self.board[y,x] = '#'
    for i in range(1,power+1):
      if y - i >= 0:
        if self.board[y-i,x] == '.':
          self.board[y-i,x] = '#'
          numCellsExpanded += 1
        elif self.board[y-i,x] == 'W':
          self.board[y-i,x] = '#'
          numCellsExpanded += 1
          self.done = True
          return numCellsExpanded - power
        else: 
          numCellsExpanded += 1
          i -= 1
    return numCellsExpanded - power

  def right(self,piece):
    print("right")
    numCellsExpanded = 0
    x = int(piece[2])
    y = int(piece[1])
    power = int(piece[0])
    self.board[y,x] = '#'
    for i in range(1,power+1):
      if x + i < self.c:
        if self.board[y,x +i] == '.':
          self.board[y,x +i] = '#'
          numCellsExpanded += 1
        elif self.board[y,x +i] == 'W':
          self.board[y,x+i] = '#'
          numCellsExpanded += 1
          self.done = True
          return numCellsExpanded - power
        else: 
          numCellsExpanded += 1
          i -= 1
    return numCellsExpanded - power

  def left(self,piece):
    print("left")
    numCellsExpanded = 0
    x = int(piece[2])
    y = int(piece[1])
    power = int(piece[0])
    self.board[y,x] = '#'
    for i in range(1,power+1):
      if x - i >= 0:
        if self.board[y,x-i] == '.':
          self.board[y,x-i] = '#'
          numCellsExpanded += 1
        elif self.board[y,x-i] == 'W':
          self.board[y,x-i] = '#'
          numCellsExpanded += 1
          self.done = True
          return numCellsExpanded - power
        else: 
          numCellsExpanded += 1
          i -= 1
    return numCellsExpanded - power


"""
if __name__== "__main__":
  env = ZhedEnv("level1.txt")
  env.render()
  while not env.done:
    if len(env.pieces) == 0:
      env.reset()
    else:
      env.step(env.action_space.sample())
      env.render()
"""  
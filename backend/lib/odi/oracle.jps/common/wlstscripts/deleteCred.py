
import sys

from sets import ImmutableSet as frozenset
required = frozenset(['-map', '-key'])
optional = frozenset()
import jpsCmdHelp

argmap = jpsCmdHelp.verifyArgs(required, optional, sys.argv[1:])

if argmap == None:
    jpsCmdHelp.deleteCredHelp()
    exit()

map = argmap['map']
key = argmap['key']

connect()
import jpsWlstCmd
jpsWlstCmd.deleteCred(map=map, key=key)


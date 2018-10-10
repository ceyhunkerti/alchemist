
import sys

from sets import ImmutableSet as frozenset
required = frozenset(['-appStripe', '-appRoleName'])
optional = frozenset()
import jpsCmdHelp

argmap = jpsCmdHelp.verifyArgs(required, optional, sys.argv[1:])

if argmap == None:
    jpsCmdHelp.createAppRoleHelp()
    exit()

appStripe = argmap['appStripe']
appRoleName = argmap['appRoleName']

connect()
import jpsWlstCmd
jpsWlstCmd.createAppRole(appStripe=appStripe, appRoleName=appRoleName)


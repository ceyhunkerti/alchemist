
import sys

from sets import ImmutableSet as frozenset
required = frozenset(['-appStripe', '-appRoleName'])
optional = frozenset()
import jpsCmdHelp

argmap = jpsCmdHelp.verifyArgs(required, optional, sys.argv[1:])

if argmap == None:
    jpsCmdHelp.deleteAppRoleHelp()
    exit()

appStripe = argmap['appStripe']
appRoleName = argmap['appRoleName']

connect()
import jpsWlstCmd
jpsWlstCmd.deleteAppRole(appStripe=appStripe, appRoleName=appRoleName)


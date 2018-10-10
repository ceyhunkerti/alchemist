
import sys

from sets import ImmutableSet as frozenset
required = frozenset(['-permClass'])
optional = frozenset(['-appStripe', '-principalName', '-principalClass', '-codeBaseURL', '-permTarget', '-permActions', '-identityDomain'])
import jpsCmdHelp

argmap = jpsCmdHelp.verifyArgs(required, optional, sys.argv[1:])

if argmap == None:
    jpsCmdHelp.grantPermissionHelp()
    exit()

permClass = argmap['permClass']
appStripe = None
codeBaseURL = None
principalClass = None
principalName = None
permTarget = None
permActions = None
identityDomain = None

if 'appStripe' in argmap:
    appStripe = argmap['appStripe']
if 'codeBaseURL' in argmap:
    codeBaseURL = argmap['codeBaseURL']
if 'principalClass' in argmap:
    principalClass = argmap['principalClass']
if 'principalName' in argmap:
    principalName = argmap['principalName']
if 'permTarget' in argmap:
    permTarget = argmap['permTarget']
if 'permActions' in argmap:
    permActions = argmap['permActions']
if 'identityDomain' in argmap:
    identityDomain = argmap['identityDomain']

connect()
import jpsWlstCmd
jpsWlstCmd.grantPermission(appStripe=appStripe, codeBaseURL=codeBaseURL, principalClass=principalClass, principalName=principalName, permClass=permClass, permTarget=permTarget, permActions=permActions, identityDomain=identityDomain)


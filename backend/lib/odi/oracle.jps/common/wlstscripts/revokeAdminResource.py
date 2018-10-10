
import sys

from sets import ImmutableSet as frozenset
required = frozenset(['-adminRoleName', 'adminResource', 'action'])
optional = frozenset(['-appStripe','-policyDomainName'])
import jpsCmdHelp

argmap = jpsCmdHelp.verifyArgs(required, optional, sys.argv[1:])

if argmap == None:
    jpsCmdHelp.revokeAdminResourceHelp()
    exit()

appStripe = None
adminRoleName = argmap['adminRoleName']
adminResource = argmap['adminResource']
action = argmap['action']
policyDomainName = None
if 'appStripe' in argmap:
        appStripe = argmap['appStripe']
if 'policyDomain' in argmap:
        policyDomainName = argmap['policyDomainName']

connect()
import Opss
Opss.revokeAdminResource(appStripe, policyDomainName, adminRoleName, adminResource, action)



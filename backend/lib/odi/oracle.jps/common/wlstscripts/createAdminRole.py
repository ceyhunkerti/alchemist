
import sys

from sets import ImmutableSet as frozenset
required = frozenset(['-adminRoleName'])
optional = frozenset(['-appStripe', '-policyDomainName','-displayName','-description'])
import jpsCmdHelp

argmap = jpsCmdHelp.verifyArgs(required, optional, sys.argv[1:])

if argmap == None:
    jpsCmdHelp.createAdminRoleHelp()
    exit()

appStripe = None
adminRoleName = argmap['adminRoleName']
policyDomainName = None
displayName = None
description = None
if 'appStripe' in argmap:
        appStripe = argmap['appStripe']
if 'policyDomain' in argmap:
        policyDomainName = argmap['policyDomainName']
if 'displayName' in argmap:
        displayName = argmap['displayName']
if 'description' in argmap:
        description = argmap['description']

connect()
import Opss
Opss.createAdminRole(appStripe=appStripe, policyDomainName=policyDomainName, adminRoleName=adminRoleName, displayName=displayName, description=description)


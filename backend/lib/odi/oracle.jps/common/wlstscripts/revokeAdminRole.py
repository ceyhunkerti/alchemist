
import sys

from sets import ImmutableSet as frozenset
required = frozenset(['-adminRoleName', '-principalClass', '-principalName'])
optional = frozenset(['-appStripe','-policyDomainName'])
import jpsCmdHelp

argmap = jpsCmdHelp.verifyArgs(required, optional, sys.argv[1:])

if argmap == None:
    jpsCmdHelp.revokeAdminRoleHelp()
    exit()

appStripe = None
adminRoleName = argmap['adminRoleName']
principalClass = argmap['principalClass']
principalName = argmap['principalName']
policyDomainName = None
if 'appStripe' in argmap:
        appStripe = argmap['appStripe']
if 'policyDomain' in argmap:
        policyDomainName = argmap['policyDomainName']

connect()
import Opss
Opss.revokeAdminRole(appStripe, policyDomainName, adminRoleName, principalClass, principalName)


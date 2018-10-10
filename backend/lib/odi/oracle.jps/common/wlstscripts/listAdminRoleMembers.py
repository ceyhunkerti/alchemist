
import sys

from sets import ImmutableSet as frozenset
required = frozenset(['-adminRoleName'])
optional = frozenset(['-appStripe', '-policyDomainName'])
import jpsCmdHelp

argmap = jpsCmdHelp.verifyArgs(required, optional, sys.argv[1:])

if argmap == None:
    jpsCmdHelp.listAdminRoleMembersHelp()
    exit()

appStripe = None
adminRoleName = argmap['adminRoleName']
policyDomainName = None
if 'appStripe' in argmap:
        appStripe = argmap['appStripe']
if 'policyDomain' in argmap:
        policyDomainName = argmap['policyDomainName']

connect()
import Opss
Opss.listAdminRoleMembers(appStripe=None, policyDomainName=None, adminRoleName=None)


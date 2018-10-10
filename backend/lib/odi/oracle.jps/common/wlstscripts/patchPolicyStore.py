
import sys

from sets import ImmutableSet as frozenset
required = frozenset(['-phase', '-productionJpsConfig'])
optional = frozenset(['-patchDeltaFolder', '-baselineFile', '-patchFile', '-baselineAppStripe', '-productionAppStripe', '-patchAppStripe', '-silent', '-ignoreEnterpriseMembersOfAppRole', '-reportFile', '-ignoreEnterpriseAppRoleMembershipConflicts', '-tagMetaData', '-patchMetaData', '-overwriteToFactory', '-forceTag', '-useSeedDataInProdutionAsBaseline'])
import jpsCmdHelp

argmap = jpsCmdHelp.verifyArgs(required, optional, sys.argv[1:])

if argmap == None:
    jpsCmdHelp.patchPolicyStoreHelp()
    exit()

phase = argmap['phase']
baselineFile = None
patchFile = None
productionJpsConfig = argmap['productionJpsConfig']
patchDeltaFolder = None
baselineAppStripe = None
productionAppStripe = None
patchAppStripe = None
silent = "false"
ignoreEnterpriseMembersOfAppRole = None
reportFile = None
ignoreEnterpriseAppRoleMembershipConflicts = None
tagMetaData = None
patchMetaData = None
forceTag = None
overwriteToFactory = None
useSeedDataInProdutionAsBaseline = None

if 'baselineFile' in argmap:
    baselineFile = argmap['baselineFile']
if 'patchFile' in argmap:
    patchFile = argmap['patchFile']
if 'patchDeltaFolder' in argmap:
    patchDeltaFolder = argmap['patchDeltaFolder']
if 'baselineAppStripe' in argmap:
    baselineAppStripe = argmap['baselineAppStripe']
if 'productionAppStripe' in argmap:
    productionAppStripe = argmap['productionAppStripe']
if 'patchAppStripe' in argmap:
    patchAppStripe = argmap['patchAppStripe']
if 'silent' in argmap:
    silent = argmap['silent']
if 'ignoreEnterpriseMembersOfAppRole' in argmap:
    ignoreEnterpriseMembersOfAppRole = argmap['ignoreEnterpriseMembersOfAppRole']
if 'reportFile' in argmap:
    reportFile = argmap['reportFile']
if 'ignoreEnterpriseAppRoleMembershipConflicts' in argmap:
    ignoreEnterpriseAppRoleMembershipConflicts = argmap['ignoreEnterpriseAppRoleMembershipConflicts']
if 'tagMetaData' in argmap:
    tagMetaData = argmap['tagMetaData']
if 'patchMetaData' in argmap:
    patchMetaData = argmap['patchMetaData']
if 'overwriteToFactory' in argmap:
    overwriteToFactory = argmap['overwriteToFactory']
if 'forceTag' in argmap:
    forceTag = argmap['forceTag']
if 'useSeedDataInProdutionAsBaseline' in argmap:
    useSeedDataInProdutionAsBaseline = argmap['useSeedDataInProdutionAsBaseline']

import jpsWlstCmd
jpsWlstCmd.patchPolicyStore(phase=phase, patchDeltaFolder=patchDeltaFolder, productionJpsConfig=productionJpsConfig, baselineFile=baselineFile, patchFile=patchFile, baselineAppStripe=baselineAppStripe, productionAppStripe=productionAppStripe, patchAppStripe=patchAppStripe, silent=silent, ignoreEnterpriseMembersOfAppRole=ignoreEnterpriseMembersOfAppRole, reportFile=reportFile, ignoreEnterpriseAppRoleMembershipConflicts=ignoreEnterpriseAppRoleMembershipConflicts, tagMetaData=tagMetaData, patchMetaData=patchMetaData, overwriteToFactory=overwriteToFactory, forceTag=forceTag,useSeedDataInProdutionAsBaseline=useSeedDataInProdutionAsBaseline)


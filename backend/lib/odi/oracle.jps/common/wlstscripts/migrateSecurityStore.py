
import sys

from sets import ImmutableSet as frozenset
required = frozenset(['-type'])
optional = frozenset(['-configFile', '-src', '-dst', '-fromFileStore', '-toFileStore', '-srcApp', '-dstApp', '-srcFolder', '-dstFolder', '-dstLdifFile', '-srcConfigFile', '-processPrivRole', '-resourceTypeFile', '-overWrite', '-migrateIdStoreMapping', '-dropUsers', '-preserveAppRoleGuid', '-reportFile', '-mode', '-metaData', '-bootCredLoc', '-encryptSSP'])
import jpsCmdHelp
argmap = jpsCmdHelp.verifyArgs(required, optional, sys.argv[1:])
if argmap == None:
	jpsCmdHelp.MigrateSecurityStoreHelp()
	exit()

type = argmap['type']
configFile = None
srcApp = None
dstApp = None
srcFolder = None
dstFolder = None
dstLdifFile = None
srcConfigFile = None
processPrivRole = None
resourceTypeFile = None
overWrite = None
migrateIdStoreMapping = None
dropUsers = None
preserveAppRoleGuid = None
reportFile = None
mode = None
metaData = None
src = None
dst = None
fromFileStore = None
toFileStore = None
bootCredLoc = None
encryptSSP = None
if 'configFile' in argmap:
        configFile = argmap['configFile']
if 'src' in argmap:
	src = argmap['src']
if 'dst' in argmap:
	dst = argmap['dst']
if 'fromFileStore' in argmap:
	fromFileStore = argmap['fromFileStore']
if 'toFileStore' in argmap:
	toFileStore = argmap['toFileStore']
if 'srcApp' in argmap:
	srcApp = argmap['srcApp']
if 'dstApp' in argmap:
	dstApp = argmap['dstApp']
if 'srcFolder' in argmap:
	srcFolder = argmap['srcFolder']
if 'dstFolder' in argmap:
	dstFolder = argmap['dstFolder']
if 'dstLdifFile' in argmap:
	dstLdifFile = argmap['dstLdifFile']
if 'srcConfigFile' in argmap:
	srcConfigFile = argmap['srcConfigFile']
if 'processPrivRole' in argmap:
	processPrivRole = argmap['processPrivRole']
if 'resourceTypeFile' in argmap:
	resourceTypeFile = argmap['resourceTypeFile']
if 'overWrite' in argmap:
	overWrite = argmap['overWrite']
if 'migrateIdStoreMapping' in argmap:
	migrateIdStoreMapping = argmap['migrateIdStoreMapping']
if 'dropUsers' in argmap:
        dropUsers = argmap['dropUsers']
if 'preserveAppRoleGuid' in argmap:
	preserveAppRoleGuid = argmap['preserveAppRoleGuid']
if 'reportFile' in argmap:
	reportFile = argmap['reportFile']
if 'mode' in argmap:
	mode = argmap['mode']
if 'metaData' in argmap:
        metaData = argmap['metaData']
if 'bootCredLoc' in argmap:
        bootCredLoc = argmap['bootCredLoc']
if 'encryptSSP' in argmap:
        encryptSSP = argmap['encryptSSP']

import jpsWlstCmd
jpsWlstCmd.migrateSecurityStore(type=type, src=src, dst=dst, srcApp=srcApp, dstApp=dstApp, srcFolder=srcFolder, dstFolder=dstFolder, dstLdifFile=dstLdifFile, srcConfigFile=srcConfigFile, configFile=configFile, processPrivRole=processPrivRole, resourceTypeFile=resourceTypeFile, overWrite=overWrite, migrateIdStoreMapping=migrateIdStoreMapping, dropUsers=dropUsers, preserveAppRoleGuid=preserveAppRoleGuid, reportFile=reportFile, mode=mode, metaData=metaData, fromFileStore=fromFileStore, toFileStore=toFileStore, bootCredLoc=bootCredLoc, encryptSSP=encryptSSP)
exit()

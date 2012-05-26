/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.security;

import org.eclipse.emf.cdo.etypes.EtypesPackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.security.SecurityFactory
 * @model kind="package"
 * @generated
 */
public interface SecurityPackage extends EPackage
{
  /**
   * The package name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNAME = "security"; //$NON-NLS-1$

  /**
   * The package namespace URI.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_URI = "http://www.eclipse.org/emf/CDO/security/4.1.0"; //$NON-NLS-1$

  /**
   * The package namespace name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "security"; //$NON-NLS-1$

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  SecurityPackage eINSTANCE = org.eclipse.emf.cdo.security.impl.SecurityPackageImpl.init();

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.security.impl.SecurityElementImpl <em>Element</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.security.impl.SecurityElementImpl
   * @see org.eclipse.emf.cdo.security.impl.SecurityPackageImpl#getSecurityElement()
   * @generated
   */
  int SECURITY_ELEMENT = 0;

  /**
   * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SECURITY_ELEMENT__ANNOTATIONS = EtypesPackage.MODEL_ELEMENT__ANNOTATIONS;

  /**
   * The number of structural features of the '<em>Element</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SECURITY_ELEMENT_FEATURE_COUNT = EtypesPackage.MODEL_ELEMENT_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.security.impl.RealmImpl <em>Realm</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.security.impl.RealmImpl
   * @see org.eclipse.emf.cdo.security.impl.SecurityPackageImpl#getRealm()
   * @generated
   */
  int REALM = 2;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.security.impl.DirectoryImpl <em>Directory</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.security.impl.DirectoryImpl
   * @see org.eclipse.emf.cdo.security.impl.SecurityPackageImpl#getDirectory()
   * @generated
   */
  int DIRECTORY = 3;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.security.impl.SecurityItemImpl <em>Item</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.security.impl.SecurityItemImpl
   * @see org.eclipse.emf.cdo.security.impl.SecurityPackageImpl#getSecurityItem()
   * @generated
   */
  int SECURITY_ITEM = 1;

  /**
   * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SECURITY_ITEM__ANNOTATIONS = SECURITY_ELEMENT__ANNOTATIONS;

  /**
   * The number of structural features of the '<em>Item</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SECURITY_ITEM_FEATURE_COUNT = SECURITY_ELEMENT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REALM__ANNOTATIONS = SECURITY_ELEMENT__ANNOTATIONS;

  /**
   * The feature id for the '<em><b>Items</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REALM__ITEMS = SECURITY_ELEMENT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REALM__NAME = SECURITY_ELEMENT_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Realm</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REALM_FEATURE_COUNT = SECURITY_ELEMENT_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DIRECTORY__ANNOTATIONS = SECURITY_ITEM__ANNOTATIONS;

  /**
   * The feature id for the '<em><b>Items</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DIRECTORY__ITEMS = SECURITY_ITEM_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DIRECTORY__NAME = SECURITY_ITEM_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Directory</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DIRECTORY_FEATURE_COUNT = SECURITY_ITEM_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.security.impl.RoleImpl <em>Role</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.security.impl.RoleImpl
   * @see org.eclipse.emf.cdo.security.impl.SecurityPackageImpl#getRole()
   * @generated
   */
  int ROLE = 4;

  /**
   * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ROLE__ANNOTATIONS = SECURITY_ITEM__ANNOTATIONS;

  /**
   * The feature id for the '<em><b>Assignees</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ROLE__ASSIGNEES = SECURITY_ITEM_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ROLE__ID = SECURITY_ITEM_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Type</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ROLE__TYPE = SECURITY_ITEM_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>Role</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ROLE_FEATURE_COUNT = SECURITY_ITEM_FEATURE_COUNT + 3;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.security.impl.AssigneeImpl <em>Assignee</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.security.impl.AssigneeImpl
   * @see org.eclipse.emf.cdo.security.impl.SecurityPackageImpl#getAssignee()
   * @generated
   */
  int ASSIGNEE = 5;

  /**
   * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ASSIGNEE__ANNOTATIONS = SECURITY_ITEM__ANNOTATIONS;

  /**
   * The feature id for the '<em><b>Roles</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ASSIGNEE__ROLES = SECURITY_ITEM_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ASSIGNEE__ID = SECURITY_ITEM_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Assignee</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ASSIGNEE_FEATURE_COUNT = SECURITY_ITEM_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.security.impl.GroupImpl <em>Group</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.security.impl.GroupImpl
   * @see org.eclipse.emf.cdo.security.impl.SecurityPackageImpl#getGroup()
   * @generated
   */
  int GROUP = 6;

  /**
   * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GROUP__ANNOTATIONS = ASSIGNEE__ANNOTATIONS;

  /**
   * The feature id for the '<em><b>Roles</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GROUP__ROLES = ASSIGNEE__ROLES;

  /**
   * The feature id for the '<em><b>Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GROUP__ID = ASSIGNEE__ID;

  /**
   * The feature id for the '<em><b>Inherited Groups</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GROUP__INHERITED_GROUPS = ASSIGNEE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Inheriting Groups</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GROUP__INHERITING_GROUPS = ASSIGNEE_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Users</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GROUP__USERS = ASSIGNEE_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>Group</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GROUP_FEATURE_COUNT = ASSIGNEE_FEATURE_COUNT + 3;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.security.impl.UserImpl <em>User</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.security.impl.UserImpl
   * @see org.eclipse.emf.cdo.security.impl.SecurityPackageImpl#getUser()
   * @generated
   */
  int USER = 7;

  /**
   * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int USER__ANNOTATIONS = ASSIGNEE__ANNOTATIONS;

  /**
   * The feature id for the '<em><b>Roles</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int USER__ROLES = ASSIGNEE__ROLES;

  /**
   * The feature id for the '<em><b>Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int USER__ID = ASSIGNEE__ID;

  /**
   * The feature id for the '<em><b>Groups</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int USER__GROUPS = ASSIGNEE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>First Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int USER__FIRST_NAME = ASSIGNEE_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Last Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int USER__LAST_NAME = ASSIGNEE_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Email</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int USER__EMAIL = ASSIGNEE_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Locked</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int USER__LOCKED = ASSIGNEE_FEATURE_COUNT + 4;

  /**
   * The feature id for the '<em><b>Password</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int USER__PASSWORD = ASSIGNEE_FEATURE_COUNT + 5;

  /**
   * The number of structural features of the '<em>User</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int USER_FEATURE_COUNT = ASSIGNEE_FEATURE_COUNT + 6;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.security.impl.UserPasswordImpl <em>User Password</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.security.impl.UserPasswordImpl
   * @see org.eclipse.emf.cdo.security.impl.SecurityPackageImpl#getUserPassword()
   * @generated
   */
  int USER_PASSWORD = 8;

  /**
   * The feature id for the '<em><b>Encrypted</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int USER_PASSWORD__ENCRYPTED = 0;

  /**
   * The number of structural features of the '<em>User Password</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int USER_PASSWORD_FEATURE_COUNT = 1;

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.security.SecurityElement <em>Element</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Element</em>'.
   * @see org.eclipse.emf.cdo.security.SecurityElement
   * @generated
   */
  EClass getSecurityElement();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.security.Realm <em>Realm</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Realm</em>'.
   * @see org.eclipse.emf.cdo.security.Realm
   * @generated
   */
  EClass getRealm();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.security.Realm#getItems <em>Items</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Items</em>'.
   * @see org.eclipse.emf.cdo.security.Realm#getItems()
   * @see #getRealm()
   * @generated
   */
  EReference getRealm_Items();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.security.Realm#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.security.Realm#getName()
   * @see #getRealm()
   * @generated
   */
  EAttribute getRealm_Name();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.security.Directory <em>Directory</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Directory</em>'.
   * @see org.eclipse.emf.cdo.security.Directory
   * @generated
   */
  EClass getDirectory();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.security.Directory#getItems <em>Items</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Items</em>'.
   * @see org.eclipse.emf.cdo.security.Directory#getItems()
   * @see #getDirectory()
   * @generated
   */
  EReference getDirectory_Items();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.security.Directory#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.security.Directory#getName()
   * @see #getDirectory()
   * @generated
   */
  EAttribute getDirectory_Name();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.security.SecurityItem <em>Item</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Item</em>'.
   * @see org.eclipse.emf.cdo.security.SecurityItem
   * @generated
   */
  EClass getSecurityItem();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.security.Role <em>Role</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Role</em>'.
   * @see org.eclipse.emf.cdo.security.Role
   * @generated
   */
  EClass getRole();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.security.Role#getAssignees <em>Assignees</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Assignees</em>'.
   * @see org.eclipse.emf.cdo.security.Role#getAssignees()
   * @see #getRole()
   * @generated
   */
  EReference getRole_Assignees();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.security.Role#getId <em>Id</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Id</em>'.
   * @see org.eclipse.emf.cdo.security.Role#getId()
   * @see #getRole()
   * @generated
   */
  EAttribute getRole_Id();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.security.Role#getType <em>Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Type</em>'.
   * @see org.eclipse.emf.cdo.security.Role#getType()
   * @see #getRole()
   * @generated
   */
  EAttribute getRole_Type();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.security.Assignee <em>Assignee</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Assignee</em>'.
   * @see org.eclipse.emf.cdo.security.Assignee
   * @generated
   */
  EClass getAssignee();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.security.Assignee#getRoles <em>Roles</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Roles</em>'.
   * @see org.eclipse.emf.cdo.security.Assignee#getRoles()
   * @see #getAssignee()
   * @generated
   */
  EReference getAssignee_Roles();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.security.Assignee#getId <em>Id</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Id</em>'.
   * @see org.eclipse.emf.cdo.security.Assignee#getId()
   * @see #getAssignee()
   * @generated
   */
  EAttribute getAssignee_Id();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.security.Group <em>Group</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Group</em>'.
   * @see org.eclipse.emf.cdo.security.Group
   * @generated
   */
  EClass getGroup();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.security.Group#getUsers <em>Users</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Users</em>'.
   * @see org.eclipse.emf.cdo.security.Group#getUsers()
   * @see #getGroup()
   * @generated
   */
  EReference getGroup_Users();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.security.Group#getInheritedGroups <em>Inherited Groups</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Inherited Groups</em>'.
   * @see org.eclipse.emf.cdo.security.Group#getInheritedGroups()
   * @see #getGroup()
   * @generated
   */
  EReference getGroup_InheritedGroups();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.security.Group#getInheritingGroups <em>Inheriting Groups</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Inheriting Groups</em>'.
   * @see org.eclipse.emf.cdo.security.Group#getInheritingGroups()
   * @see #getGroup()
   * @generated
   */
  EReference getGroup_InheritingGroups();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.security.User <em>User</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>User</em>'.
   * @see org.eclipse.emf.cdo.security.User
   * @generated
   */
  EClass getUser();

  /**
   * Returns the meta object for the reference list '{@link org.eclipse.emf.cdo.security.User#getGroups <em>Groups</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Groups</em>'.
   * @see org.eclipse.emf.cdo.security.User#getGroups()
   * @see #getUser()
   * @generated
   */
  EReference getUser_Groups();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.security.User#getFirstName <em>First Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>First Name</em>'.
   * @see org.eclipse.emf.cdo.security.User#getFirstName()
   * @see #getUser()
   * @generated
   */
  EAttribute getUser_FirstName();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.security.User#getLastName <em>Last Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Last Name</em>'.
   * @see org.eclipse.emf.cdo.security.User#getLastName()
   * @see #getUser()
   * @generated
   */
  EAttribute getUser_LastName();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.security.User#getEmail <em>Email</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Email</em>'.
   * @see org.eclipse.emf.cdo.security.User#getEmail()
   * @see #getUser()
   * @generated
   */
  EAttribute getUser_Email();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.security.User#isLocked <em>Locked</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Locked</em>'.
   * @see org.eclipse.emf.cdo.security.User#isLocked()
   * @see #getUser()
   * @generated
   */
  EAttribute getUser_Locked();

  /**
   * Returns the meta object for the containment reference '{@link org.eclipse.emf.cdo.security.User#getPassword <em>Password</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Password</em>'.
   * @see org.eclipse.emf.cdo.security.User#getPassword()
   * @see #getUser()
   * @generated
   */
  EReference getUser_Password();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.security.UserPassword <em>User Password</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>User Password</em>'.
   * @see org.eclipse.emf.cdo.security.UserPassword
   * @generated
   */
  EClass getUserPassword();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.security.UserPassword#getEncrypted <em>Encrypted</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Encrypted</em>'.
   * @see org.eclipse.emf.cdo.security.UserPassword#getEncrypted()
   * @see #getUserPassword()
   * @generated
   */
  EAttribute getUserPassword_Encrypted();

  /**
   * Returns the factory that creates the instances of the model.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the factory that creates the instances of the model.
   * @generated
   */
  SecurityFactory getSecurityFactory();

  /**
   * <!-- begin-user-doc -->
   * Defines literals for the meta objects that represent
   * <ul>
   *   <li>each class,</li>
   *   <li>each feature of each class,</li>
   *   <li>each enum,</li>
   *   <li>and each data type</li>
   * </ul>
   * <!-- end-user-doc -->
   * @generated
   */
  interface Literals
  {
    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.security.impl.SecurityElementImpl <em>Element</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.security.impl.SecurityElementImpl
     * @see org.eclipse.emf.cdo.security.impl.SecurityPackageImpl#getSecurityElement()
     * @generated
     */
    EClass SECURITY_ELEMENT = eINSTANCE.getSecurityElement();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.security.impl.RealmImpl <em>Realm</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.security.impl.RealmImpl
     * @see org.eclipse.emf.cdo.security.impl.SecurityPackageImpl#getRealm()
     * @generated
     */
    EClass REALM = eINSTANCE.getRealm();

    /**
     * The meta object literal for the '<em><b>Items</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference REALM__ITEMS = eINSTANCE.getRealm_Items();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute REALM__NAME = eINSTANCE.getRealm_Name();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.security.impl.DirectoryImpl <em>Directory</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.security.impl.DirectoryImpl
     * @see org.eclipse.emf.cdo.security.impl.SecurityPackageImpl#getDirectory()
     * @generated
     */
    EClass DIRECTORY = eINSTANCE.getDirectory();

    /**
     * The meta object literal for the '<em><b>Items</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference DIRECTORY__ITEMS = eINSTANCE.getDirectory_Items();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute DIRECTORY__NAME = eINSTANCE.getDirectory_Name();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.security.impl.SecurityItemImpl <em>Item</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.security.impl.SecurityItemImpl
     * @see org.eclipse.emf.cdo.security.impl.SecurityPackageImpl#getSecurityItem()
     * @generated
     */
    EClass SECURITY_ITEM = eINSTANCE.getSecurityItem();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.security.impl.RoleImpl <em>Role</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.security.impl.RoleImpl
     * @see org.eclipse.emf.cdo.security.impl.SecurityPackageImpl#getRole()
     * @generated
     */
    EClass ROLE = eINSTANCE.getRole();

    /**
     * The meta object literal for the '<em><b>Assignees</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ROLE__ASSIGNEES = eINSTANCE.getRole_Assignees();

    /**
     * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ROLE__ID = eINSTANCE.getRole_Id();

    /**
     * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ROLE__TYPE = eINSTANCE.getRole_Type();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.security.impl.AssigneeImpl <em>Assignee</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.security.impl.AssigneeImpl
     * @see org.eclipse.emf.cdo.security.impl.SecurityPackageImpl#getAssignee()
     * @generated
     */
    EClass ASSIGNEE = eINSTANCE.getAssignee();

    /**
     * The meta object literal for the '<em><b>Roles</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ASSIGNEE__ROLES = eINSTANCE.getAssignee_Roles();

    /**
     * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ASSIGNEE__ID = eINSTANCE.getAssignee_Id();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.security.impl.GroupImpl <em>Group</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.security.impl.GroupImpl
     * @see org.eclipse.emf.cdo.security.impl.SecurityPackageImpl#getGroup()
     * @generated
     */
    EClass GROUP = eINSTANCE.getGroup();

    /**
     * The meta object literal for the '<em><b>Users</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference GROUP__USERS = eINSTANCE.getGroup_Users();

    /**
     * The meta object literal for the '<em><b>Inherited Groups</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference GROUP__INHERITED_GROUPS = eINSTANCE.getGroup_InheritedGroups();

    /**
     * The meta object literal for the '<em><b>Inheriting Groups</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference GROUP__INHERITING_GROUPS = eINSTANCE.getGroup_InheritingGroups();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.security.impl.UserImpl <em>User</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.security.impl.UserImpl
     * @see org.eclipse.emf.cdo.security.impl.SecurityPackageImpl#getUser()
     * @generated
     */
    EClass USER = eINSTANCE.getUser();

    /**
     * The meta object literal for the '<em><b>Groups</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference USER__GROUPS = eINSTANCE.getUser_Groups();

    /**
     * The meta object literal for the '<em><b>First Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute USER__FIRST_NAME = eINSTANCE.getUser_FirstName();

    /**
     * The meta object literal for the '<em><b>Last Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute USER__LAST_NAME = eINSTANCE.getUser_LastName();

    /**
     * The meta object literal for the '<em><b>Email</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute USER__EMAIL = eINSTANCE.getUser_Email();

    /**
     * The meta object literal for the '<em><b>Locked</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute USER__LOCKED = eINSTANCE.getUser_Locked();

    /**
     * The meta object literal for the '<em><b>Password</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference USER__PASSWORD = eINSTANCE.getUser_Password();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.security.impl.UserPasswordImpl <em>User Password</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.security.impl.UserPasswordImpl
     * @see org.eclipse.emf.cdo.security.impl.SecurityPackageImpl#getUserPassword()
     * @generated
     */
    EClass USER_PASSWORD = eINSTANCE.getUserPassword();

    /**
     * The meta object literal for the '<em><b>Encrypted</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute USER_PASSWORD__ENCRYPTED = eINSTANCE.getUserPassword_Encrypted();

  }

} // SecurityPackage
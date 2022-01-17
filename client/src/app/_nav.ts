import { INavData } from '@coreui/angular';

import { RoleAccess } from './models/role-access';

export const navItems: any[] = [
  // export const navItems = [
  {
    name: 'Dashboard',
    url: '/dashboard',
    icon: 'icon-speedometer',
    permissions: [
      RoleAccess.viewUsers,
      RoleAccess.viewRoles,
      RoleAccess.viewVirtualAccounts,
    ],
  },
  {
    name: 'User Management',
    url: '/users',
    icon: 'icon-user',
    permissions: [RoleAccess.viewUsers, RoleAccess.viewRoles],
    children: [
      {
        name: 'Users',
        url: '/users',
        icon: 'fa fa-users ml-2',
        permissions: [RoleAccess.viewUsers],
      },
      {
        name: 'Roles',
        url: '/roles',
        icon: 'fa fa-registered ml-2',
        permissions: [RoleAccess.viewRoles],
      },
    ],
  },
  {
    name: 'Account Managment',
    url: '/virtual-accounts',
    icon: 'fa fa-cogs',
    permissions: [
      RoleAccess.viewVirtualAccounts,
      RoleAccess.getVirtualAccounts,
      RoleAccess.addVirtualAccount,
      RoleAccess.editVirtualAccount,
      RoleAccess.viewCancelPayouts,
    ],
    children: [
      {
        name: 'Payout Account List',
        url: '/virtual-accounts',
        icon: 'fa fa-list ml-1',
        permissions: [
          RoleAccess.viewVirtualAccounts,
        ],
      },
      {
        name: 'Add Payout Account',
        url: '/virtual-accounts/add-virtual-account',
        icon: 'fa fa-plus-circle ml-1',
        permissions: [RoleAccess.addVirtualAccount],
      },
    ]
  },
  {
    name: 'Account Operation',
    url: '/virtual-accounts',
    icon: 'fa fa-list',
    permissions: [
      RoleAccess.viewVirtualAccounts,
      RoleAccess.getVirtualAccounts,
      RoleAccess.addVirtualAccount,
      RoleAccess.editVirtualAccount,
      RoleAccess.viewFileUpload,
      RoleAccess.viewCancelPayouts,
    ],
    children: [
      {
        name: 'Cancel Payouts',
        url: '/cancel-payouts',
        icon: 'fa fa-ban ml-1',
        permissions: [RoleAccess.viewCancelPayouts],
      },
      {
        name: 'Insta Payouts',
        url: '/virtual-accounts/add-insta-payout',
        icon: 'fa fa-plus-circle ml-1',
        permissions: [RoleAccess.addInstaPayout],
      },
      // {
      //   name: 'Self Payouts',
      //   url: '/virtual-accounts/add-self-payout',
      //   icon: 'fa fa-plus-circle ml-1',
      //   permissions: [RoleAccess.addInstaPayout],
      // },
      {
        name: 'File Upload',
        url: '/virtual-accounts/file-upload',
        icon: 'fa fa-upload ml-1',
        permissions: [RoleAccess.viewFileUpload],
      },
      {
        name: 'Account Statement',
        url: '/virtual-accounts/statement',
        icon: 'fa fa-upload ml-1',
        permissions: [RoleAccess.viewAccountStatement],
      },
    ]
  },

  {
    name: 'Configuration',
    url: '/configuration/view',
    icon: 'fa fa-sliders',
    permissions: [
      RoleAccess.viewMerchantConfigurations,
      RoleAccess.editMerchantConfigurations,
    ],
  },
  {
    name: 'Payout Details',
    url: '/reports',
    icon: 'fa fa-file',
    permissions: [
      RoleAccess.viewReports
    ],
  },
];

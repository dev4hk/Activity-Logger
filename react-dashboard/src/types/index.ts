export interface UserActivityEvent {
    id: string;
    userId: string;
    activityType: string;
    details: string;
    timestamp: string;
}


export interface UserActivityRequest {
    userId: string;
    activityType: string;
    details: string;
}


export const ACTIVITY_TYPES = [
    'LOGIN',
    'LOGOUT',
    'VIEW_PAGE',
    'ADD_TO_CART',
    'CHECKOUT',
    'UPDATE_PROFILE'
] as const;

export type ActivityType = typeof ACTIVITY_TYPES[number];

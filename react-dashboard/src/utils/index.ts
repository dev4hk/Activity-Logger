import type {ActivityType} from "../types";

export const getActivityColor = (activityType: ActivityType): string => {
    const colors: Record<ActivityType, string> = {
        LOGIN: 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200',
        LOGOUT: 'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-200',
        VIEW_PAGE: 'bg-blue-100 text-blue-800 dark:bg-blue-900 dark:text-blue-200',
        ADD_TO_CART: 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-200',
        CHECKOUT: 'bg-purple-100 text-purple-800 dark:bg-purple-900 dark:text-purple-200',
        UPDATE_PROFILE: 'bg-indigo-100 text-indigo-800 dark:bg-indigo-900 dark:text-indigo-200'
    };
    return colors[activityType] || 'bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-300';
};

export const formatTimestamp = (timestamp: string): string => {
    return new Date(timestamp).toLocaleString();
};

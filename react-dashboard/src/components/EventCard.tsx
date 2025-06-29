import React from 'react';
import { getActivityColor, formatTimestamp } from '../utils';
import type {ActivityType, UserActivityEvent} from "../types";

interface EventCardProps {
    event: UserActivityEvent;
}

const EventCard: React.FC<EventCardProps> = ({ event }) => (
    <div className="border border-gray-200 rounded-lg p-4 hover:bg-gray-50 transition-colors dark:border-gray-700 dark:bg-gray-700 dark:hover:bg-gray-600 text-gray-900 dark:text-gray-100">
        <div className="flex justify-between items-start mb-2">
            <div className="flex items-center space-x-3">
                <span className={`px-2 py-1 rounded-full text-xs font-medium ${getActivityColor(event.activityType as ActivityType)}`}>
                    {event.activityType}
                </span>
                <span className="text-sm text-gray-600 font-mono dark:text-gray-300">
                    User: {event.userId}
                </span>
            </div>
            <span className="text-xs text-gray-500 dark:text-gray-400">
                {formatTimestamp(event.timestamp)}
            </span>
        </div>
        <p className="text-sm text-gray-700 mb-1 dark:text-gray-200">{event.details}</p>
        <p className="text-xs text-gray-500 font-mono dark:text-gray-400">ID: {event.id}</p>
    </div>
);

export default EventCard;

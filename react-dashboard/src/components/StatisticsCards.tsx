import React from 'react';
import { getActivityColor } from '../utils';
import {ACTIVITY_TYPES, type ActivityType, type UserActivityEvent} from "../types";

interface StatisticsCardsProps {
    events: UserActivityEvent[];
}

const StatisticsCards: React.FC<StatisticsCardsProps> = ({ events }) => (
    <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
        {ACTIVITY_TYPES.map((type: ActivityType) => { // Explicitly type 'type'
            const count = events.filter(e => e.activityType === type).length;
            return (
                <div key={type} className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-4 text-center text-gray-900 dark:text-gray-100">
                    <div className={`inline-block px-2 py-1 rounded-full text-xs font-medium mb-2 ${getActivityColor(type)}`}>
                        {type}
                    </div>
                    <div className="text-2xl font-bold">{count}</div>
                    <div className="text-xs text-gray-500 dark:text-gray-400">events</div>
                </div>
            );
        })}
    </div>
);

export default StatisticsCards;

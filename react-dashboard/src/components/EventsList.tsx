import React from 'react';
import EventCard from './EventCard';
import type {UserActivityEvent} from "../types";

interface EventsListProps {
    events: UserActivityEvent[];
    filterUserId: string;
    onFilterChange: (filter: string) => void;
    isLoading: boolean;
    onRefresh: () => void;
}

const EventsList: React.FC<EventsListProps> = ({ events, filterUserId, onFilterChange, isLoading, onRefresh }) => {
    const filteredEvents = filterUserId.trim()
        ? events.filter(event => event.userId.toLowerCase().includes(filterUserId.toLowerCase()))
        : events;

    return (
        <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6 text-gray-900 dark:text-gray-100">
            <div className="flex justify-between items-center mb-4">
                <h2 className="text-xl font-semibold">Recent Activities</h2>
                <div className="flex items-center space-x-2">
                    <input
                        type="text"
                        value={filterUserId}
                        onChange={(e) => onFilterChange(e.target.value)}
                        className="px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm dark:bg-gray-700 dark:border-gray-600"
                        placeholder="Filter by User ID..."
                    />
                    <button
                        onClick={onRefresh}
                        disabled={isLoading}
                        className="px-3 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:opacity-50 text-sm dark:bg-blue-700 dark:hover:bg-blue-600"
                    >
                        {isLoading ? 'Loading...' : 'Refresh'}
                    </button>
                    <span className="text-sm text-gray-500 dark:text-gray-400">
                        {filteredEvents.length} events
                    </span>
                </div>
            </div>
            <div className="space-y-3 max-h-96 overflow-y-auto">
                {filteredEvents.length === 0 ? (
                    <div className="text-center py-8 text-gray-500 dark:text-gray-400">
                        {isLoading ? 'Loading events...' : 'No events found. Try publishing an event!'}
                    </div>
                ) : (
                    filteredEvents.map((event) => (
                        <EventCard key={event.id} event={event} />
                    ))
                )}
            </div>
        </div>
    );
};

export default EventsList;

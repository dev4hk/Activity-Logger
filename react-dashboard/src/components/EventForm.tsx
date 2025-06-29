import React from 'react';
import ErrorMessage from './ErrorMessage';
import SuccessMessage from './SuccessMessage';
import {ACTIVITY_TYPES, type ActivityType, type UserActivityRequest} from "../types";

interface EventFormProps {
    formData: UserActivityRequest;
    onFormChange: (data: UserActivityRequest) => void;
    onSubmit: () => void;
    isLoading: boolean;
    error: string;
    onErrorDismiss: () => void;
    successMessage: string;
}

const EventForm: React.FC<EventFormProps> = ({
                                                 formData,
                                                 onFormChange,
                                                 onSubmit,
                                                 isLoading,
                                                 error,
                                                 onErrorDismiss,
                                                 successMessage
                                             }) => {
    const generateRandomUserId = () => {
        const randomUUID = crypto.randomUUID();
        onFormChange({ ...formData, userId: randomUUID });
    };

    const isFormValid = formData.userId.trim() && formData.details.trim();

    return (
        <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6 text-gray-900 dark:text-gray-100">
            <h2 className="text-xl font-semibold mb-4">Publish New Event</h2>

            {error && <ErrorMessage message={error} onDismiss={onErrorDismiss} />}
            {successMessage && <SuccessMessage message={successMessage} />}

            <div className="space-y-4">
                <div>
                    <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                        User ID
                    </label>
                    <div className="flex space-x-2">
                        <input
                            type="text"
                            value={formData.userId}
                            onChange={(e) => onFormChange({ ...formData, userId: e.target.value })}
                            className="flex-1 px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:border-gray-600"
                            placeholder="Enter UUID or generate random"
                        />
                        <button
                            onClick={generateRandomUserId}
                            className="px-3 py-2 bg-gray-600 text-white rounded-md hover:bg-gray-700 text-sm dark:bg-gray-700 dark:hover:bg-gray-600"
                        >
                            Random
                        </button>
                    </div>
                </div>

                <div>
                    <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                        Activity Type
                    </label>
                    <select
                        value={formData.activityType}
                        onChange={(e) => onFormChange({ ...formData, activityType: e.target.value as ActivityType })}
                        className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:border-gray-600"
                    >
                        {ACTIVITY_TYPES.map(type => (
                            <option key={type} value={type}>{type}</option>
                        ))}
                    </select>
                </div>

                <div>
                    <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                        Details
                    </label>
                    <textarea
                        value={formData.details}
                        onChange={(e) => onFormChange({ ...formData, details: e.target.value })}
                        className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:border-gray-600"
                        rows={3}
                        placeholder="Enter activity details..."
                    />
                </div>

                <button
                    onClick={onSubmit}
                    disabled={isLoading || !isFormValid}
                    className="w-full px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:opacity-50 font-medium dark:bg-blue-700 dark:hover:bg-blue-600"
                >
                    {isLoading ? 'Publishing...' : 'Publish Event'}
                </button>
            </div>
        </div>
    );
};

export default EventForm;

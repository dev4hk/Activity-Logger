import React from 'react';

interface ErrorMessageProps {
    message: string;
    onDismiss: () => void;
}

const ErrorMessage: React.FC<ErrorMessageProps> = ({ message, onDismiss }) => (
    <div className="mb-4 p-3 bg-red-100 border border-red-400 text-red-700 rounded flex justify-between items-center dark:bg-red-900 dark:border-red-700 dark:text-red-200">
        <span>{message}</span>
        <button onClick={onDismiss} className="text-red-500 hover:text-red-700 dark:text-red-300 dark:hover:text-red-100">×</button>
    </div>
);

export default ErrorMessage;
